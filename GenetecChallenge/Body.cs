using System;

namespace GenetecChallenge
{
    class Body
    {
        public DateTimeOffset LicensePlateCaptureTime { get; set; }
        public string LicensePlate { get; set; }
        public double Latitude { get; set; }
        public double Longitude { get; set; }
        public byte[] ContextImageJpg { get; set; }
        public byte[] LicensePlateImageJpg { get; set; }

        public BodySend ToBodySend(string contextImageReference)
        {
            return new BodySend
            {
                LicensePlateCaptureTime = LicensePlateCaptureTime,
                LicensePlate = LicensePlate,
                Latitude = Latitude,
                Longitude = Longitude,
                ContextImageReference = contextImageReference
            };
        }
    }
}
