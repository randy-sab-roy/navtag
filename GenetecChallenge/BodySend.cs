using System;

namespace GenetecChallenge
{
    class BodySend
    {
        public DateTimeOffset LicensePlateCaptureTime { get; set; }
        public string LicensePlate { get; set; }
        public double Latitude { get; set; }
        public double Longitude { get; set; }
        public string ContextImageReference { get; set; }
    }
}
