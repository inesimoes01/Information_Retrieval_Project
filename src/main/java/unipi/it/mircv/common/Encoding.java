package unipi.it.mircv.common;


    public class Encoding {

        public static String toUnary(int number) {
            if (number < 0) {
                throw new IllegalArgumentException("Number must be non-negative");
            }

            StringBuilder unary = new StringBuilder();
            for (int i = 0; i < number; i++) {
                unary.append("1");
            }
            unary.append("0");

            return unary.toString();
        }

        public int fromUnary(String unary) {
            int count = 0;
            for (char c : unary.toCharArray()) {
                if (c == '1') {
                    count++;
                } else {
                    break; // Interrompe il conteggio quando incontra un carattere diverso da '1'
                }
            }
            return count;
        }


        public static String toBinaryString(int number, int bitLength) {
            if (bitLength <= 0) {
                throw new IllegalArgumentException("bitLength must be positive");
            }

            String binaryString = Integer.toBinaryString(number);
            int paddingLength = bitLength - binaryString.length();

            // Aggiungi zeri all'inizio se la lunghezza della stringa binaria è inferiore a bitLength
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < paddingLength; i++) {
                sb.append("0");
            }
            sb.append(binaryString);

            return sb.toString();
        }






        public static void main(String[] args) {
            Encoding encoding = new Encoding();
            System.out.print(encoding.toUnary(1));
        }
    }
