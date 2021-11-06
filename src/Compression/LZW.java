package Compression;

import jdk.jfr.Unsigned;

import java.io.*;
import java.util.ArrayList;

public class LZW {

    public void compress(String input, String tagsOutput, String bytesOutput) {
        try {
            File file = new File(input);
            byte[] data = new byte[(int) file.length()];
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(data);
            fileInputStream.close();

            ArrayList<String> dictionary = new ArrayList<String>();

            StringBuilder inputString = new StringBuilder();

            for (int i = 0; i < data.length; ++i) {
                inputString.append((char) data[i]);
            }

            String T = "";
            int pointer = 0;

            FileWriter fileWriter = new FileWriter(tagsOutput); // write in tagsOutput file
            fileWriter.write("<Index In Dictionary>\n");

            FileOutputStream fileOutputStream = new FileOutputStream(bytesOutput);

            for (int i = 0; i < inputString.length(); ++i) {
                T += inputString.charAt(i);
                boolean check = false;
                if (T.length() == 1) {
                    for (int j = 0; j < 128; ++j) {
                        if (T.charAt(0) == ((char) j)) {
                            pointer = j;
                            check = true;
                            break;
                        }
                    }
                }
                for (int j = 0; j < dictionary.size(); ++j) {
                    if (T.equals(dictionary.get(j))) {
                        pointer = j + 128;
                        check = true;
                        break;
                    }
                }
                if (!check) {
                    fileWriter.write("<" + pointer + ">\n");
                    fileOutputStream.write((byte) pointer);
                    dictionary.add(T);
                    if (dictionary.size() == (1 << 8) - 128) {
                        dictionary.clear();
                        pointer = 0;
                        T = "";
                    }
                    T = "";
                    --i;
                } else if (i + 1 == inputString.length()) {
                    fileWriter.write("<" + pointer + ">\n");
                    fileOutputStream.write((byte) pointer);
                }
            }
            fileWriter.close();
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void decompress(String bytesInput, String output) {
        try {
            FileInputStream fileInputStream = new FileInputStream(bytesInput);

            ArrayList<String> dictionary = new ArrayList<String>();

            byte temp;

            String outputString = "";
            String lastTag = "";
            int counter = 0;
            while (true) {
                temp = (byte) fileInputStream.read();
                if (temp == -1) {
                    break;
                }
                int pointer = Byte.toUnsignedInt(temp);

                ++counter;
                boolean found = false;
                if (counter == 3) {
                    counter = counter;
                }
                if (pointer < 128) {
                    outputString+= (char) pointer;
                    if (!lastTag.equals("")) {
                        dictionary.add(lastTag + (char) pointer);
                    }
                    lastTag = "" + (char)pointer;
                    found = true;
                } else {
                    for (int j = 0; j < dictionary.size(); ++j) {
                        if (j + 128 == pointer) {
                            outputString+= dictionary.get(j);
                            found = true;
                            dictionary.add(lastTag + dictionary.get(j).charAt(0));
                            lastTag = dictionary.get(j);
                            break;
                        }
                    }
                }
                if (!found) {
                    lastTag+= (char) lastTag.charAt(0);
                    outputString+= lastTag;
                    dictionary.add(lastTag);
                }
                if (dictionary.size() == (1 << 8) - 128) {
                    dictionary.clear();
                    pointer = 0;
                }
            }
            FileWriter fileWriter = new FileWriter(output);

            fileWriter.write(outputString);

            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
