import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.ArrayList;

public class Utils {

    private final static String SEP = ": ";

    public static String encrypt(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<String, String> loadUsersFromFile(String archivo) {
        HashMap<String, String> users = new HashMap<>();

        synchronized (archivo) {
            try {
                File file = new File(archivo);
                File path = new File(file.getParent());
                path.mkdirs();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(SEP, 2);
                    String username = parts[0].trim();
                    String encryptedPassword = parts[1].trim();
                    users.put(username, encryptedPassword);
                }
                reader.close();

            } catch (Exception e) {
                System.out.println("Error en la funcion loadUsersFromFile: " + e);
            }
        }

        return users;
    }

    public static void saveUsersToFile(HashMap<String, String> users, String archivo) {
        synchronized (archivo) {
            try {
                File file = new File(archivo);
                File path = new File(file.getParent());
                path.mkdirs();
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                for (HashMap.Entry<String, String> entry : users.entrySet()) {
                    String line = entry.getKey() + SEP + entry.getValue();
                    writer.write(line);
                    writer.newLine();
                }
                writer.close();
            } catch (Exception e) {
                System.out.println("Error en la funcion saveUsersToFile: " + e);
            }
        }
    }

    public static HashMap<String, ArrayList<String>> loadFriendsFromFile(String archivo) {
        HashMap<String, ArrayList<String>> friends = new HashMap<>();

        synchronized (archivo) {
            try {
                File file = new File(archivo);
                File path = new File(file.getParent());
                path.mkdirs();

                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;

                while ((line = reader.readLine()) != null) {

                    String[] parts = line.split(SEP, 2);
                    String username = parts[0].trim();
                    String friendsStr = parts[1].trim();

                    ArrayList<String> friendsList = new ArrayList<>();
                    if (!friendsStr.isEmpty()) {
                        String[] friendsArr = friendsStr.split("\\s+");

                        for (String friend : friendsArr) {
                            friendsList.add(friend);
                        }

                    }

                    friends.put(username, friendsList);
                }
                reader.close();

            } catch (Exception e) {
                System.out.println("Error en la funcion loadFriendsFromFile: " + e);
            }
        }

        return friends;
    }

    public static void saveFriendsToFile(HashMap<String, ArrayList<String>> friends, String archivo) {

        synchronized (archivo) {
            try {
                File file = new File(archivo);
                File path = new File(file.getParent());
                path.mkdirs();

                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                for (HashMap.Entry<String, ArrayList<String>> entry : friends.entrySet()) {
                    String username = entry.getKey();
                    ArrayList<String> amigosList = entry.getValue();

                    String amigosStr = String.join(" ", amigosList);
                    String line = username + SEP + amigosStr;

                    writer.write(line);
                    writer.newLine();
                }
                writer.close();
            } catch (Exception e) {
                System.out.println("Error en la funcion saveFriendsToFile: " + e);
            }
        }
    }
}
