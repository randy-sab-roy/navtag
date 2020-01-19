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

            var localPath = "./data/";
            var guid = "image" + Guid.NewGuid().ToString();
            var fileName = guid + ".jpg";
            var localFilePath = Path.Combine(localPath, fileName);
            await File.WriteAllBytesAsync(localFilePath, bodyParsed.ContextImageJpg);
            var blobClient = containerClient.GetBlobClient(fileName);
            using FileStream uploadFileStream = File.OpenRead(localFilePath);
            var info = await blobClient.UploadAsync(uploadFileStream);
            uploadFileStream.Close();

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

                        Console.WriteLine("\nWANTED 2 : " + bodyParsed.LicensePlate);

                        break;
                    }
                    else
                    {
                        Console.Write(trimed + "     ");
                    }
                }

                if (!found)
                {
                    Console.WriteLine("\nNot wanted : " + bodyParsed.LicensePlate);
                    var localFilePath2 = Path.Combine(localPath, guid + ".txt");
                    await File.WriteAllLinesAsync(localFilePath2, result.recognitionResult.lines.Select(s => s.text));
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
