package tk.freetobuild.mcunified;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import sk.tomsik68.mclauncher.api.servers.ServerInfo;
import sk.tomsik68.mclauncher.impl.servers.PingedServerInfo;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by liz on 6/28/16.
 */
public class ServerPinger {
    public static PingedServerInfo pingServer(ServerInfo info) throws IOException {
        InetSocketAddress host = new InetSocketAddress(info.getIP(), info.getPort());
        Main.logger.info("Pinging "+info.getIP()+":"+info.getPort());
        Socket socket = new Socket();
        OutputStream outputStream;
        DataOutputStream dataOutputStream;
        InputStream inputStream;
        InputStreamReader inputStreamReader;
        socket.setSoTimeout(7000);
        socket.connect(host, 7000);
        outputStream = socket.getOutputStream();
        dataOutputStream = new DataOutputStream(outputStream);
        inputStream = socket.getInputStream();
        inputStreamReader = new InputStreamReader(inputStream);
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream handshake = new DataOutputStream(b);
        handshake.writeByte(0x00);
        writeVarInt(handshake, 4);
        writeVarInt(handshake, host.getHostString().length());
        handshake.writeBytes(host.getHostString());
        handshake.writeShort(host.getPort());
        writeVarInt(handshake, 1);
        writeVarInt(dataOutputStream, b.size());
        dataOutputStream.write(b.toByteArray());
        dataOutputStream.writeByte(0x01);
        dataOutputStream.writeByte(0x00);
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        readVarInt(dataInputStream); //size (unused)
        int id = readVarInt(dataInputStream);
        if (id == -1) {
            throw new IOException("Premature end of stream.");
        }
        if (id != 0x00) {
            throw new IOException("Invalid packetID");
        }
        int length = readVarInt(dataInputStream);
        if (length == -1) {
            throw new IOException("Premature end of stream.");
        }
        if (length == 0) {
            throw new IOException("Invalid string length.");
        }
        byte[] in = new byte[length];
        dataInputStream.readFully(in);  //read json string
        String json = new String(in);
        long now = System.currentTimeMillis();
        dataOutputStream.writeByte(0x09); //size of packet
        dataOutputStream.writeByte(0x01); //0x01 for ping
        dataOutputStream.writeLong(now); //time!?
        readVarInt(dataInputStream);
        id = readVarInt(dataInputStream);
        if (id == -1) {
            throw new IOException("Premature end of stream.");
        }
        if (id != 0x01) {
            throw new IOException("Invalid packetID");
        }
        dataInputStream.readLong();
        dataOutputStream.close();
        outputStream.close();
        inputStreamReader.close();
        inputStream.close();
        socket.close();
        JSONObject obj = (JSONObject) JSONValue.parse(json);
        return new UnifiedPingedServerInfo(info, obj);
    }
    private static int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) throw new RuntimeException("VarInt too big");
            if ((k & 0x80) != 128) break;
        }
        return i;
    }

    private static void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.writeByte(paramInt);
                return;
            }

            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

}
