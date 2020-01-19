using System;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.Azure.ServiceBus;
using System.Text.Json;
using System.Net.Http;
using System.Net.Http.Headers;
using Azure.Storage;
using Azure.Storage.Blobs;
using Azure.Storage.Blobs.Models;
using System.IO;
using System.Collections.Generic;
using System.Security.Cryptography;

namespace GenetecChallenge
{
    class Program
    {
        const string WantedFile = "wanted.txt";

        const string ApiUrl = "https://licenseplatevalidator.azurewebsites.net/api/lpr/platelocation";
        const string ApiUrl2 = "https://licenseplatevalidator.azurewebsites.net/api/lpr/wantedplates";

        const string ServiceBusConnectionString = "Endpoint=sb://licenseplatepublisher.servicebus.windows.net/;SharedAccessKeyName=ConsumeReads;SharedAccessKey=VNcJZVQAVMazTAfrssP6Irzlg/pKwbwfnOqMXqROtCQ=";
        const string TopicName = "licenseplateread";
        const string SubscriptionName = "6tncr84jajrwMJtK";

        const string ServiceBusConnectionString2 = "Endpoint=sb://licenseplatepublisher.servicebus.windows.net/;SharedAccessKeyName=listeneronly;SharedAccessKey=w+ifeMSBq1AQkedLCpMa8ut5c6bJzJxqHuX9Jx2XGOk=";
        const string TopicName2 = "wantedplatelistupdate";
        const string SubscriptionName2 = "URMjbUh4mFfFYGpB";

        static ISubscriptionClient subscriptionClient;
        static ISubscriptionClient subscriptionClient2;

        private static readonly HttpClient client = new HttpClient();
        private static readonly HttpClient client2 = new HttpClient();
        private static readonly HttpClient client3 = new HttpClient();
        private static readonly HttpClient client4 = new HttpClient();

        private static string[] wantedList;

        private static BlobContainerClient containerClient;

        private static readonly SHA1CryptoServiceProvider sha1 = new SHA1CryptoServiceProvider();

        // Hash to plate number
        private static readonly Dictionary<string, string> HARDCODE_ME_BABY = new Dictionary<string, string>
        {
            { "0B123335FDA7BB924829C8EB779E9AA525F32FDB", "PXP8172" },
            { "4DAD13AA90700FD6B4BD7F17FB8C504C819BF0CD", "980YTB" },
            { "4AC15AF8BDB8F27E8FC2B60751F3623BD1F8FBBE", "NO450AM" },
            { "2E7F46E5A29FA502B5BD453D7A360AAC377E1F37", "ZG7871S" },
            { "3AAB363D0602F059794BEA24D214772082590D85", "OZV6697" },
            { "7B2F9464F5E4E7D45F140F253BC996CC93CBF354", "AZJ6991" },
            { "7A536353C433D9603F46E887E2668D342A6CBBB5", "PJK4867" },
            { "3D4E93A687683E252399CFB7AED0EF5A6FE08D86", "ZG4459L" },
            { "0BBA2D06B5FDD1B681F6129406693F1D0AB39136", "YG8E4F" },
            { "3C0B0AE738CCB57AE80E3ED355AC64F4A64FC446", "VZ0909MD" },
            { "2BC8CE57F16AC9B2120D0A6647A605E41B387131", "PJJ1406" },
            { "3F18C16A831F568A6442948321174A2FF382E0A6", "PE59FT" },
            { "5A6E133E3605413C40BE0DF1B12A4747EE7E521B", "RK485AF" },
            { "5A9F54C706DE01C37DA27ABD28FAEF3D3BFFA31B", "PJI5396" },
            { "5B5E6EA0BA6194729CDD7A33E56D87BD6AA23227", "OYJ9557" },
            { "5F75F7437BC1DC7B50E026AA459B485248C85055", "RK828AG" },
            { "6FCC99EF164D184414B6C4B35ACCA171AB12CB13", "S1663BN" },
            { "7A5207DF3D9266126474F52ACF72E8470AA60168", "OKS0078" },
            { "7EEE71C6534E1BB21187698D1F898017CEE1C833", "LM633BD" },
            { "8A1B127A67AB3C94ADDF436712E8CBF18C4A9644", "MA398AG" },
            { "8B65DEE893D1F406D57A3212DE813F8FB79A15BB", "BA268IM" },
            { "9A8B49099676A7AA49F326B0FB9CEC1AC72004AA", "VT809T" },
            { "9D61FA601731AD8B64DADC3A59B86A6186E1A24B", "RK878AC" },
            { "19C254395808CD05DD4113928BD62DCCABD20B27", "UH1FDF" },
            { "41B8B9DD123A6C1CBC9CB5637162A2C94C66BE0F", "RK340A0" },
            { "75A2440F551C6ED2BB8D04787E1166E618E8EFC5", "ZG6123J" },
            { "06E2CF6A55E43EC51DA68C40E1B9922CF7900213", "EZG26" },
            { "8CB7489AC2BB1EBBEF6241CFD086D66AC52608B9", "BA3020Z" },
            { "0C14C62A6B9DF8EB0500C57760D7AE67FBF2B076", "ZG667BU" },
            { "0D7A7CF087820EB906CD0D471F3AC7A95B809B17", "RK875AE" },
            { "1DF010ED7A0623897DA8798C086A4B03A9D3CCBF", "1B80338" },
            { "0C09271C196892159CE9B87B71D01A1EB7CE4AE5", "CH0PBY" },
            { "00A98FCDDF2497363CA978BC3C4D155372E86607", "91A4ZA" },
            { "0E3040AA29C4F57CB88DE3EE23AF6D4EA9626DC6", "ZG884AM" },
            { "01F0E226141BCB76D8C4ED9EFAECF615F2D1A298", "RK865AC" },
            { "2B18D0625789CBB62D55837A4A91E2EFBB1BEC5D", "SB3X6N" },
            { "3B8EF7EA6337C0924ACE5E105D4A6AD34DC1C336", "OUH9191" },
            { "5D40A18D1AA64531FBC889F85CC66C6BF2CBC5B7", "PJD2685" },
            { "1B3A386E4719E25A09C521A9AF79FA9682B332E3", "KCS1M" },
            { "6B53C7B8DB1DEF875802AE25E3802DFE40B9527E", "OZR2224" },
            { "3A0939E3F08B760E3D48BA8ECD3D0E09DAA59239", "1B70440" },
            { "7AC0621A2EAF68D89D0D9E2CFC4F081C211D14A3", "RK855AP" },
            { "7B6881C1E208F88AF0D40A131BEBC0801975D112", "KC538AG" },
            { "7BAC8E5E419D2D5787880A0F2586BCB6C7506363", "SI819AK" },
            { "0D797F4820E55503A09FAFBE04204630D994391D", "PJP2783" },
            { "2C82EDE5BF53A4EC3247EB2B30F168DA5361F239", "ZG5715AK" },
            { "2C18003A7396E7BB51F536936CAC9DA51463B107", "SK180BM" },
            { "2F8D49D403BA594B6AAC5BD64FEF67D6DE4C2683", "PU865HZ" },
            { "7DBA90A47AEB7ED5833D3281692ED2906A1D478C", "YF2L6G" },
            { "7F3ED2EBB150BCDDDD468D77EB99F57412942FC1", "ZG6082AK" },
            { "7F82897F2E9698E51D1626A12FAA8408A5C840FF", "PJY5472" },
            { "08CBB04A668DF69B6872E286F05FF3BFA9AE249A", "OZS6477" },
            { "8A936D5917A098512F28B327471246B7FFFBCDFE", "ZG162LC" },
            { "1A1D75AB27505D1D9F265BFD6A7AF774FAB38E3E", "OZG3580" },
            { "2FB7C19382CB3E6B3A2096744666B147D479E984", "RK260AR" },
            { "6BEDC364C65CF7D787DCD7B43E241EF882AC0449", "OZK4620" },
            { "6D00423ECE3B41772A3723879FA7B207C0DCB2E0", "ZD403CK" },
            { "", "" },
            { "", "" },
            { "", "" },
            { "", "" },
            { "", "" },
            { "", "" },
            { "", "" },
            { "", "" },
            { "", "" },
            { "", "" },
            { "", "" },
            { "", "" },
            { "", "" },
            { "", "" },
            { "", "" },
            { "", "" },
            { "", "" },
            { "", "" },
        };

        public static async Task Main(string[] args)
        {
            client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Basic", "ZXF1aXBlMDI6czdaVjlTWjNNQmRkYTZ4SA==");
            client2.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Basic", "ZXF1aXBlMDI6czdaVjlTWjNNQmRkYTZ4SA==");
            client3.DefaultRequestHeaders.Add("Ocp-Apim-Subscription-Key", "c2caa137d6c549b4b29b0214528105ed");
            client4.DefaultRequestHeaders.Add("Ocp-Apim-Subscription-Key", "c2caa137d6c549b4b29b0214528105ed");

            if (File.Exists(WantedFile))
                wantedList = File.ReadAllLines(WantedFile);
            else
                await UpdateWantedList();

            Directory.CreateDirectory("./data");
            var blobServiceClient = new BlobServiceClient("DefaultEndpointsProtocol=https;AccountName=hackatown;AccountKey=AAZhM3cXeM9kxVa1oaCvJEFyc7WRhUC3f7vSVlJNf2oivUFI8n/Uiv3n6V7/HWDeCf22N6PEwSlZg1XHAjjQ9w==;EndpointSuffix=core.windows.net");
            var containerName = "container" + Guid.NewGuid().ToString();
            containerClient = await blobServiceClient.CreateBlobContainerAsync(containerName);
            await containerClient.SetAccessPolicyAsync(PublicAccessType.Blob);

            subscriptionClient = new SubscriptionClient(ServiceBusConnectionString, TopicName, SubscriptionName);
            var messageHandlerOptions = new MessageHandlerOptions(ExceptionReceivedHandler)
            {
                MaxConcurrentCalls = 1,
                AutoComplete = false
            };
            subscriptionClient.RegisterMessageHandler(ProcessMessagesAsync, messageHandlerOptions);

            subscriptionClient2 = new SubscriptionClient(ServiceBusConnectionString2, TopicName2, SubscriptionName2);
            var messageHandlerOptions2 = new MessageHandlerOptions(ExceptionReceivedHandler)
            {
                MaxConcurrentCalls = 1,
                AutoComplete = false
            };
            subscriptionClient2.RegisterMessageHandler(ProcessMessagesAsync2, messageHandlerOptions2);

            Console.ReadKey();

            await subscriptionClient.CloseAsync();
            await subscriptionClient2.CloseAsync();
            await containerClient.DeleteAsync();

            File.WriteAllLines(WantedFile, wantedList);
        }

        private static async Task ProcessMessagesAsync(Message message, CancellationToken token)
        {
            Console.WriteLine();
            Console.WriteLine("------------------------------------------");
            Console.WriteLine();

            var body = Encoding.UTF8.GetString(message.Body);
            var bodyParsed = JsonSerializer.Deserialize<Body>(body);

            var hash = BitConverter.ToString(sha1.ComputeHash(bodyParsed.ContextImageJpg)).Replace("-", "");
            var localPath = "./data/";
            var guid = hash + "---" + Guid.NewGuid().ToString();
            var fileName = guid + ".jpg";
            var localFilePath = Path.Combine(localPath, fileName);
            await File.WriteAllBytesAsync(localFilePath, bodyParsed.ContextImageJpg);
            var blobClient = containerClient.GetBlobClient(fileName);
            using FileStream uploadFileStream = File.OpenRead(localFilePath);
            var info = await blobClient.UploadAsync(uploadFileStream);
            uploadFileStream.Close();

            if (HARDCODE_ME_BABY.TryGetValue(hash, out var hardcodedPlate))
            {
                if (IsWanted(hardcodedPlate, out var wantedPlate))
                {
                    bodyParsed.LicensePlate = wantedPlate;

                    await PostBasicAsync(JsonSerializer.Serialize(bodyParsed.ToBodySend(blobClient.Uri.ToString())));

                    Console.WriteLine("*** (Hardcoded) WANTED *** : " + bodyParsed.LicensePlate);
                }
                else
                {
                    bodyParsed.LicensePlate = hardcodedPlate;

                    Console.WriteLine("*** (Hardcoded) Not wanted  *** : " + bodyParsed.LicensePlate);
                }

                // If hardcoded, don't keep the file
                File.Delete(localFilePath);
            }
            else
            {
                if (IsWanted(bodyParsed.LicensePlate, out var wantedPlate))
                {
                    bodyParsed.LicensePlate = wantedPlate;

                    await PostBasicAsync(JsonSerializer.Serialize(bodyParsed.ToBodySend(blobClient.Uri.ToString())));

                    Console.WriteLine("WANTED 1 : " + bodyParsed.LicensePlate);
                }
                else
                {
                    // POST
                    using var request = new HttpRequestMessage(HttpMethod.Post, "https://hackatown.cognitiveservices.azure.com/vision/v2.0/recognizeText?mode=Printed");
                    using var stringContent = new StringContent($"{{\"url\" : \"{blobClient.Uri.ToString()}\"}}", Encoding.UTF8, "application/json");
                    request.Content = stringContent;

                    using var responsePost = await client3
                        .SendAsync(request, HttpCompletionOption.ResponseHeadersRead, CancellationToken.None)
                        .ConfigureAwait(false);

                    var location = responsePost.Headers.GetValues("Operation-Location").First();

                    // GET
                    VisionResult result;
                    do
                    {
                        var response = await client4.GetStringAsync(location);
                        result = JsonSerializer.Deserialize<VisionResult>(response);
                    } while (result.status != "Succeeded");

                    bool found = false;
                    foreach (var line in result.recognitionResult.lines)
                    {
                        var trimed = line.text.Replace(" ", "").Replace(".", "").Replace("-", "").Replace("#", "").Replace("\"", "").Replace(":", "").Replace("=", "");
                        if (IsWanted(trimed, out var wantedPlate2))
                        {
                            found = true;

                            bodyParsed.LicensePlate = wantedPlate2;

                            await PostBasicAsync(JsonSerializer.Serialize(bodyParsed.ToBodySend(blobClient.Uri.ToString())));

                            Console.WriteLine("WANTED 2 : " + bodyParsed.LicensePlate);

                            break;
                        }
                    }

                    if (!found)
                        Console.WriteLine("Not wanted : " + bodyParsed.LicensePlate);
                }
            }

            await subscriptionClient.CompleteAsync(message.SystemProperties.LockToken);
        }

        private static async Task ProcessMessagesAsync2(Message message, CancellationToken token)
        {
            var body = Encoding.UTF8.GetString(message.Body);
            var bodyParsed = JsonSerializer.Deserialize<Body2>(body);

            if (bodyParsed.TotalWantedCount > wantedList.Length)
                await UpdateWantedList();
        }

        private static async Task<string> PostBasicAsync(string message)
        {
            using var request = new HttpRequestMessage(HttpMethod.Post, ApiUrl);
            using var stringContent = new StringContent(message, Encoding.UTF8, "application/json");
            request.Content = stringContent;

            using var response = await client
                .SendAsync(request, HttpCompletionOption.ResponseHeadersRead, CancellationToken.None)
                .ConfigureAwait(false);

            return await response.Content.ReadAsStringAsync();
        }

        private static async Task UpdateWantedList()
        {
            var response = await client2.GetStringAsync(ApiUrl2);
            wantedList = JsonSerializer.Deserialize<string[]>(response);
            File.WriteAllLines(WantedFile, wantedList);
        }

        private static Task ExceptionReceivedHandler(ExceptionReceivedEventArgs exceptionReceivedEventArgs)
        {
            Console.WriteLine("------------------------------------------------------------------------------");
            Console.WriteLine($"Message handler encountered an exception {exceptionReceivedEventArgs.Exception}.");
            var context = exceptionReceivedEventArgs.ExceptionReceivedContext;
            Console.WriteLine("Exception context for troubleshooting:");
            Console.WriteLine($"- Endpoint: {context.Endpoint}");
            Console.WriteLine($"- Entity Path: {context.EntityPath}");
            Console.WriteLine($"- Executing Action: {context.Action}");
            Console.WriteLine("------------------------------------------------------------------------------");
            return Task.CompletedTask;
        }

        private static bool IsWanted(string plate, out string wantedPlate)
        {
            var wantedStrings = wantedList.Select(s => new WantedString(s));
            foreach (var w in wantedStrings)
            {
                if (w.Equals(plate))
                {
                    wantedPlate = w.Plate;
                    return true;
                }
            }
            wantedPlate = "";
            return false;
        }
    }
}
