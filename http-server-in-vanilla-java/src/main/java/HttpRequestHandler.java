import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;

class HttpRequestHandler{
    Socket clientSocket;
    String directory;
    String EOL = "\r\n";
    String[] headers= new String[50];

    public HttpRequestHandler(Socket clientSocket, String directory){
        this.clientSocket = clientSocket;
        this.directory = directory;
        for (int i = 0; i < headers.length; i++) {
            headers[i] = "";
        }
    }

    public byte[] gzipCompression(byte[] fileContent){
        try{
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(arrayOutputStream);
            gzip.write(fileContent);
            gzip.finish();
            return arrayOutputStream.toByteArray();

        } catch (IOException e) {
            return fileContent;
        }

    }

    public String getHeader(String name){
        int i = 0;
        while(!headers[i].equals(name)){
            i = i+2;
            if(headers[i] == ""){
                return headers[i];
            }
        }
        return headers[i+1];
    }

    public void sendResponse(OutputStream outputStream, int statusCode, String statusPhrase) throws IOException {
        StringBuilder response = new StringBuilder("HTTP/1.1 ");
        response.append(statusCode).append(" ");
        response.append(statusPhrase).append(EOL).append(EOL);
        System.out.println("[RESPONSE] "+ response);
        outputStream.write(response.toString().getBytes());

    }

    public void sendResponse(OutputStream outputStream,int statusCode, String statusPhrase,String[] header, String contentType, String body )throws IOException{
        StringBuilder response;
        response = new StringBuilder("HTTP/1.1 ");
        response.append(statusCode);
        response.append(" ").append(statusPhrase);
        response.append(EOL);
        if(header.length % 2!= 0){
            throw new IOException();
        }
        for(int i = 0 ; i < header.length; i=i+2){
            response.append(header[i]).append(": ").append(header[i + 1]).append(EOL);
        }
        response.append("Content-Type: ").append(contentType).append(EOL);
        response.append("Content-Length: ").append(body.length()).append(EOL);
        response.append(EOL);
        response.append(body);
        System.out.println("[RESPONSE] "+ response);
        outputStream.write(response.toString().getBytes());
    }

    public void sendResponse(OutputStream outputStream,int statusCode, String statusPhrase,String[] header, String contentType, byte[] body)throws IOException{
        StringBuilder response;
        response = new StringBuilder("HTTP/1.1 ");
        response.append(statusCode);
        response.append(" ").append(statusPhrase);
        response.append(EOL);
        if(header.length % 2!= 0){
            throw new IOException();
        }
        for(int i = 0 ; i < header.length; i=i+2){
            response.append(header[i]).append(": ").append(header[i + 1]).append(EOL);
        }
        response.append("Content-Type: ").append(contentType).append(EOL);
        response.append("Content-Length: ").append(body.length).append(EOL);
        response.append(EOL);
        System.out.println("[RESPONSE] "+ response+EOL+ body);
        outputStream.write(response.toString().getBytes());
        outputStream.write(body);

    }

    public void  setHeaders(BufferedReader reader) throws IOException {
        String[] temp ;
        int i = 0;
        int j;
        String line;
        while(reader.ready()){
            j = 1;
            line = reader.readLine();
            temp = line.split(" ");
            if(line.equals("")){
                return;
            }
            if(temp.length == 1){
                continue;
            }
            headers[i] = temp[0].substring(0, temp[0].length()-1);
            StringBuilder string = new StringBuilder();
            while(j != temp.length){

                if(temp[j].contains(",")){
                    string.append(temp[j]).append("\b ");
                }else{
                    string.append(temp[j]);
                }
                j++;
            }
            headers[i+1] = string.toString();
            i = i+2;
        }

    }

    public void run(){
        try{
            if(directory != null){
                System.out.println("Directory: "+ directory);
            }
            OutputStream outputStream = clientSocket.getOutputStream();
            InputStream input = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line =reader.readLine();
            String[] httpRequest = line.split(" ",  0);
            System.out.println("[REQUEST] "+line);
            setHeaders(reader);
            System.out.println(Arrays.toString(headers));
            //routing
            if(httpRequest[1].startsWith("/echo/")){
                String param = httpRequest[1].substring(6);
                String headerValue;
                headerValue= getHeader("Accept-Encoding");

                if(headerValue.contains("gzip")){
                    byte[] encodedFileContent= gzipCompression(param.getBytes());
                    sendResponse(outputStream,
                            200,
                            "OK",
                            new String[]{"Content-Encoding", "gzip"},
                            "text/plain",
                            encodedFileContent);

                }else {
                    sendResponse(outputStream,
                            200,
                            "OK",
                            new String[]{},
                            "text/plain",
                            param
                    );
                }

            }else if(httpRequest[1].equals("/")){
                sendResponse(outputStream, 200, "OK");

            }else if(httpRequest[1].equals("/user-agent")){
                sendResponse(outputStream, 200, "OK", new String[]{}, "text/plain",getHeader("User-Agent"));

                //we check whether the file name given in the request is present in out directory,
                // if it is then we send the file
            } else if (httpRequest[1].startsWith("/files/") && httpRequest[0].equals("GET")) {
                String filename = httpRequest[1].substring(7);
                File file = new File(directory, filename);

                if(file.exists()) {
                    byte[] fileContents = Files.readAllBytes(file.toPath());
                    sendResponse(outputStream,
                            200,
                            "OK",
                            new String[]{},
                            "application/octet-stream",
                            fileContents);
                }else {
                    sendResponse(outputStream, 404, "Not Found");
                }

            } else if (httpRequest[1].startsWith("/files/") && httpRequest[0].equals("POST")) {
                String filename = httpRequest[1].substring(7);
                File file = new File(directory, filename);
                try (var writer = new FileWriter(file)){
                    while (reader.ready()){
                        writer.write(reader.read());
                    }
                }
                sendResponse(outputStream, 201, "Created");

            } else {
                sendResponse(outputStream, 404, "Not Found");
            }

            clientSocket.close();

        }catch (IOException e){
            System.out.println("[IOEXCEPTION] "+ e.getMessage());
        }
    }
}
