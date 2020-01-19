namespace GenetecChallenge
{
    class VisionResult
    {
        public string status { get; set; }
        public VisionLines recognitionResult { get; set; }
    }

    class VisionLines
    {
        public VisionLine[] lines { get; set; }
    }

    class VisionLine
    {
        public int[] boundingBox { get; set; }
        public string text { get; set; }
        public VisionWord[] words { get; set; }
    }

    class VisionWord
    {
        public int[] boundingBox { get; set; }
        public string text { get; set; }
        public string confidence { get; set; }
    }
}
