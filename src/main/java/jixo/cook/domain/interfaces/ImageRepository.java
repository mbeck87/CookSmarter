package jixo.cook.domain.interfaces;

import java.io.IOException;

public interface ImageRepository {
    void downloadAndSave(String name, String url) throws IOException, InterruptedException;
    void copyLocal(String sourcePath);
    void ensureNoCoverExists() throws IOException;
}
