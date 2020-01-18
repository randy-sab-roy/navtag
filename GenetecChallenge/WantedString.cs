using System;
using System.Collections.Generic;
using System.Linq;

namespace GenetecChallenge
{
    class WantedString
    {
        public string Plate { get; }

        private readonly IEnumerable<string> possibilities;

        public WantedString(string plate)
        {
            Plate = plate;

            possibilities = GeneratePossibilities(Plate);
        }

        private IEnumerable<string> GeneratePossibilities(string origin)
        {
            if (origin.Length == 0) return Enumerable.Empty<string>();

            var allPossibleFirst = new List<char>();
            var rest = GeneratePossibilities(origin[1..]);

            switch (origin.First())
            {
                case 'B':
                case '8':
                    allPossibleFirst.Add('B');
                    allPossibleFirst.Add('8');
                    break;
                case 'C':
                case 'G':
                    allPossibleFirst.Add('C');
                    allPossibleFirst.Add('G');
                    break;
                case 'E':
                case 'F':
                    allPossibleFirst.Add('E');
                    allPossibleFirst.Add('F');
                    break;
                case 'K':
                case 'X':
                case 'Y':
                    allPossibleFirst.Add('K');
                    allPossibleFirst.Add('X');
                    allPossibleFirst.Add('Y');
                    break;
                case 'I':
                case '1':
                case 'T':
                case 'J':
                    allPossibleFirst.Add('I');
                    allPossibleFirst.Add('1');
                    allPossibleFirst.Add('T');
                    allPossibleFirst.Add('J');
                    break;
                case 'S':
                case '5':
                    allPossibleFirst.Add('S');
                    allPossibleFirst.Add('5');
                    break;
                case 'O':
                case 'D':
                case 'Q':
                case '0':
                    allPossibleFirst.Add('O');
                    allPossibleFirst.Add('D');
                    allPossibleFirst.Add('Q');
                    allPossibleFirst.Add('0');
                    break;
                case 'P':
                case 'R':
                    allPossibleFirst.Add('P');
                    allPossibleFirst.Add('R');
                    break;
                case 'Z':
                case '2':
                    allPossibleFirst.Add('Z');
                    allPossibleFirst.Add('2');
                    break;
                default:
                    allPossibleFirst.Add(origin.First());
                    break;
            }

            var allPossibilities = new List<string>();

            if(origin.Length == 1)
                foreach (var firstChar in allPossibleFirst)
                        allPossibilities.Add(firstChar.ToString());
            else
                foreach (var firstChar in allPossibleFirst)
                    foreach (var finishingString in rest)
                        allPossibilities.Add(firstChar + finishingString);

            return allPossibilities;
        }

        public bool Equals(string other)
        {
            return possibilities.Contains(other);
        }
    }
}
