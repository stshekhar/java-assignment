package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.jboss.logging.Logger;

@ApplicationScoped
public class LegacyStoreManagerGateway {

  private static final Logger LOGGER = Logger.getLogger(LegacyStoreManagerGateway.class);

  public void createStoreOnLegacySystem(Store store) {
    writeToFile(store);
  }

  public void updateStoreOnLegacySystem(Store store) {
    writeToFile(store);
  }

  private void writeToFile(Store store) {
    Path tempFile = null;
    try {
      tempFile = Files.createTempFile("store-", ".txt");
      String content =
          "Store created. [ name ="
              + store.name
              + " ] [ items on stock ="
              + store.quantityProductsInStock
              + "]";
      Files.writeString(tempFile, content, StandardCharsets.UTF_8);
      Files.readString(tempFile, StandardCharsets.UTF_8);
    } catch (IOException exception) {
      LOGGER.errorf(exception, "Failed to synchronize store with legacy system: storeId=%s", store.id);
      throw new IllegalStateException("Failed to synchronize store with legacy system.", exception);
    } finally {
      if (tempFile != null) {
        try {
          Files.deleteIfExists(tempFile);
        } catch (IOException exception) {
          LOGGER.warnf(exception, "Failed to delete temporary store sync file: storeId=%s", store.id);
        }
      }
    }
  }
}
