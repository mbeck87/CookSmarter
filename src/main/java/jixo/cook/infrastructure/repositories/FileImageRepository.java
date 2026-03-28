package jixo.cook.infrastructure.repositories;

import jixo.cook.domain.interfaces.ImageRepository;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileImageRepository implements ImageRepository {

    @Override
    public void downloadAndSave(String name, String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        client.close();

        File file = new File(System.getProperty("user.dir") + "/storage/images/" + name);
        try (FileOutputStream out = new FileOutputStream(file)) {
            out.write(response.body());
        }
    }

    @Override
    public void copyLocal(String sourcePath) {
        Path source = Paths.get(sourcePath);
        Path fileName = source.getFileName();
        Path targetDir = Paths.get(System.getProperty("user.dir") + "/storage/images/");
        Path target = targetDir.resolve(fileName);

        if (!source.equals(target)) {
            try {
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.err.println("Fehler beim Kopieren: " + e.getMessage());
            }
        }
    }

    @Override
    public void ensureNoCoverExists() throws IOException {
        File outputFile = new File(System.getProperty("user.dir") + "/storage/images/noCover.jpg");
        if (!outputFile.exists()) {
            try (InputStream stream = getClass().getModule().getResourceAsStream("jixo/cook/images/noCover.jpg")) {
                if (stream == null) throw new IOException("Resource noCover.jpg not found in module");
                Files.copy(stream, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}
