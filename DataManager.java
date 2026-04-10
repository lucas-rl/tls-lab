import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public record DataManager(
        OutputStream outputStream,
        InputStream inputStream
) {
    public byte[] getData() throws IOException {
        byte[] lengthBytes = readFixedNumBytes(inputStream, 2);
        int length = ((lengthBytes[0] & 0xFF) << 8) | (lengthBytes[1] & 0xFF);
        return readFixedNumBytes(inputStream, length);
    }

    private byte[] readFixedNumBytes(InputStream input, int size) throws IOException {
        byte[] buffer = new byte[size];
        int totalRead = 0;

        while (totalRead < size) {
            int bytesRead = input.read(buffer, totalRead, size - totalRead);

            if (bytesRead == -1) {
                throw new IOException("Connection closed before reading enough data");
            }

            totalRead += bytesRead;
        }

        return buffer;
    }

    public void sendData(byte[] payload) throws IOException {
        int length = payload.length;

        outputStream.write((length >> 8) & 0xFF); // high byte
        outputStream.write(length & 0xFF);        // low byte
        outputStream.write(payload);
        outputStream.flush();
    }

}
