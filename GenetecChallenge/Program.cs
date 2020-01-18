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

        private static string[] wantedList;

        private static BlobContainerClient containerClient;

        public static async Task Main(string[] args)
        {
            client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Basic", "ZXF1aXBlMDI6czdaVjlTWjNNQmRkYTZ4SA==");
            client2.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Basic", "ZXF1aXBlMDI6czdaVjlTWjNNQmRkYTZ4SA==");

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
            var body = Encoding.UTF8.GetString(message.Body);
            var bodyParsed = JsonSerializer.Deserialize<Body>(body);

            if (IsWanted(bodyParsed.LicensePlate, out var wantedPlate))
            {
                bodyParsed.LicensePlate = wantedPlate;

                string localPath = "./data/";
                string fileName = "image" + Guid.NewGuid().ToString() + ".jpg";
                string localFilePath = Path.Combine(localPath, fileName);
                await File.WriteAllBytesAsync(localFilePath, bodyParsed.ContextImageJpg);
                var blobClient = containerClient.GetBlobClient(fileName);
                using FileStream uploadFileStream = File.OpenRead(localFilePath);
                var info = await blobClient.UploadAsync(uploadFileStream);
                uploadFileStream.Close();

                Console.WriteLine(JsonSerializer.Serialize(bodyParsed.ToBodySend(blobClient.Uri.ToString())));
                await PostBasicAsync(JsonSerializer.Serialize(bodyParsed.ToBodySend(blobClient.Uri.ToString())));

                Console.WriteLine("WANTED : " + bodyParsed.LicensePlate);
            }
            else
            {
                Console.WriteLine("Not wanted : " + bodyParsed.LicensePlate);
            }

            await subscriptionClient.CompleteAsync(message.SystemProperties.LockToken);
        }

        private static async Task ProcessMessagesAsync2(Message message, CancellationToken token)
        {
            await UpdateWantedList();

            Console.Write("New wanted list : " + wantedList.Aggregate("", (acc, wanted) => acc + "," + wanted));

            if (token.CanBeCanceled)
                await subscriptionClient.CompleteAsync(message.SystemProperties.LockToken);
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
            foreach(var w in wantedStrings)
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
