package pl.pawel;

import java.io.*;
import java.net.*;

public class Main {

  public static void main(String[] args) {
    try {
      ServerSocket serverSocket = new ServerSocket(8080);

      while (true) {
        new ThreadSocket(serverSocket.accept());
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

class ThreadSocket implements Runnable {
  private Socket inSocket;

  ThreadSocket(Socket inSocket) {
    this.inSocket = inSocket;
    new Thread(this).start();
  }

  @Override
  public void run() {
    try {
      InputStream is = inSocket.getInputStream();
      PrintWriter out = new PrintWriter(inSocket.getOutputStream());
      BufferedReader in = new BufferedReader(new InputStreamReader(is));

      String line = in.readLine();

      String requestMethod = line;
      System.out.println(requestMethod);

      int dataLength = -1;
      while ((line = in.readLine()) != null && line.length() != 0) {
        System.out.println(line);
        if (line.contains("Content-Length:")) {
          dataLength = Integer.valueOf(
              line.substring(
                  line.indexOf("Content-Length:") + 16,
                  line.length()
              )
          );
        }
      }

      String data = "";
      if (dataLength > 0) {
        char[] chars = new char[dataLength];
        int read = in.read(chars, 0, dataLength);
        assert read != dataLength;
        data = new String(chars);
      }

      out.println("HTTP/2.0 200 OK");
      out.println("Content-Type: text/html; charset=utf-8");
      out.println("Server: EZPacket Web Server");
      out.println("");
      out.println("<h1>EZPacket makes my life easier</h1>");
      out.println("<p>You send method: " + requestMethod + "</p>");
      if (requestMethod.split(" ")[0].equals("POST")) {
        out.println("<p>You send data: " + data + "</p>");
      }
      out.close();
      inSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}