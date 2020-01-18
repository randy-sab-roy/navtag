using System;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.Azure.ServiceBus;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.Net.Http;
using System.Collections.Generic;
using System.Net.Http.Headers;

namespace GenetecChallenge
{
    class Program
    {
        const string ApiUrl = "https://licenseplatevalidator.azurewebsites.net/api/lpr/platelocation";

        const string ServiceBusConnectionString = "Endpoint=sb://licenseplatepublisher.servicebus.windows.net/;SharedAccessKeyName=ConsumeReads;SharedAccessKey=VNcJZVQAVMazTAfrssP6Irzlg/pKwbwfnOqMXqROtCQ=";
        const string TopicName = "licenseplateread";
        const string SubscriptionName = "6tncr84jajrwMJtK";

        static ISubscriptionClient subscriptionClient;

        private static readonly HttpClient client = new HttpClient();

        public static async Task Main(string[] args)
        {
            client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Basic", "ZXF1aXBlMDI6czdaVjlTWjNNQmRkYTZ4SA==");

            subscriptionClient = new SubscriptionClient(ServiceBusConnectionString, TopicName, SubscriptionName);

            var messageHandlerOptions = new MessageHandlerOptions(ExceptionReceivedHandler)
            {
                MaxConcurrentCalls = 1,
                AutoComplete = false
            };
            subscriptionClient.RegisterMessageHandler(ProcessMessagesAsync, messageHandlerOptions);

            Console.ReadKey();

            await subscriptionClient.CloseAsync();
        }

        private static async Task ProcessMessagesAsync(Message message, CancellationToken token)
        {
            var body = Encoding.UTF8.GetString(message.Body);
            var bodyParsed = JsonSerializer.Deserialize<Body>(body);
            Console.WriteLine($"LicensePlate : {bodyParsed.LicensePlate}");
            Console.WriteLine($"LicensePlateCaptureTime : {bodyParsed.LicensePlateCaptureTime}");
            Console.WriteLine($"Location : ({bodyParsed.Latitude}, {bodyParsed.Longitude})");

            var values = new Dictionary<string, string>
            {
                { "LicensePlateCaptureTime", bodyParsed.LicensePlateCaptureTime.ToString("yyyy’-‘MM’-‘dd’T’HH’:’mm’:’ss") },
                { "LicensePlate", bodyParsed.LicensePlate },
                { "Latitude", bodyParsed.Latitude.ToString("N5") },
                { "Longitude", bodyParsed.Longitude.ToString("N5") }
            };

            var content = new FormUrlEncodedContent(values);

            var response = await client.PostAsync(ApiUrl, content);

            Console.WriteLine("Response : " + await response.Content.ReadAsStringAsync());

            await subscriptionClient.CompleteAsync(message.SystemProperties.LockToken);
        }

        private static Task ExceptionReceivedHandler(ExceptionReceivedEventArgs exceptionReceivedEventArgs)
        {
            Console.WriteLine($"Message handler encountered an exception {exceptionReceivedEventArgs.Exception}.");
            var context = exceptionReceivedEventArgs.ExceptionReceivedContext;
            Console.WriteLine("Exception context for troubleshooting:");
            Console.WriteLine($"- Endpoint: {context.Endpoint}");
            Console.WriteLine($"- Entity Path: {context.EntityPath}");
            Console.WriteLine($"- Executing Action: {context.Action}");
            return Task.CompletedTask;
        }
    }
}
