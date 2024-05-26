import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class DNSMessage {
    final static int INDEX_OF_HEADER_SECTION=0;
    final static int INDEX_OF_QUESTION_SECTION=12;

    public short id;
    public short flags = (short)0b10000000_00000000;
    public short qdcount = 1;
    public short ancount = 1;
    public short nscount;
    public short arcount;
    byte[] question;
    public DNSMessage(DatagramPacket requestPacket , byte[] requestBuffer){
        question = Arrays.copyOfRange(requestPacket.getData(), INDEX_OF_QUESTION_SECTION, requestPacket.getLength());
        ByteBuffer byteBuffer = ByteBuffer.wrap(requestBuffer);
        id =byteBuffer.getShort();
        flags = byteBuffer.getShort();
        qdcount = byteBuffer.getShort();
        ancount = byteBuffer.getShort();
        nscount = byteBuffer.getShort();
        arcount = byteBuffer.getShort();
        char[] requestFlags = String.format("%16s", Integer.toBinaryString(flags))
                .replace(' ', '0')
                .toCharArray();
        requestFlags[0] = '1';
        requestFlags[13] = '1';
        flags = (short) Integer.parseInt(new String(requestFlags), 2);

    }
    public byte[] getResponse(byte[] requestBuffer){


        ByteBuffer buffer = ByteBuffer.allocate(512);
        writeHeaders(buffer);
        writeQuestion(buffer);
        writeAnswer(buffer);
        return buffer.array();


    }
    private byte[] encodeDomainName(String domain){
        try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
            for (String label : domain.split("\\.")) {
                outStream.write(label.length());
                outStream.writeBytes(label.getBytes());
            }
            outStream.write(0);
            return outStream.toByteArray();
        } catch (IOException e) {
            System.out.println("[IOEXCEPTION] "+ e.getMessage());
            throw new RuntimeException(e);
        }
    }


    private void writeHeaders(ByteBuffer byteBuffer){
        /*
         * 4.1.1. Header section format
         *
         * The header contains the following fields:
         *
         *                                     1  1  1  1  1  1
         *       0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
         *     +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
         *     |                      ID                       |
         *     +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
         *     |QR|   Opcode  |AA|TC|RD|RA|   Z    |   RCODE   |
         *     +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
         *     |                    QDCOUNT                    |
         *     +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
         *     |                    ANCOUNT                    |
         *     +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
         *     |                    NSCOUNT                    |
         *     +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
         *     |                    ARCOUNT                    |
         *     +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
         *
         * where:
         *
         * ID              A 16 bit identifier assigned by the program that
         *                 generates any kind of query.  This identifier is copied
         *                 the corresponding reply and can be used by the requester
         *                 to match up replies to outstanding queries.
         *
         * QR              A one bit field that specifies whether this message is a
         *                 query (0), or a response (1).
         *
         * OPCODE          A four bit field that specifies kind of query in this
         *                 message.  This value is set by the originator of a query
         *                 and copied into the response.  The values are:
         *
         *                 0               a standard query (QUERY)
         *
         *                 1               an inverse query (IQUERY)
         *
         *                 2               a server status request (STATUS)
         *
         *                 3-15            reserved for future use
         *
         * AA              Authoritative Answer - this bit is valid in responses,
         *                 and specifies that the responding name server is an
         *                 authority for the domain name in question section.
         *
         *                 Note that the contents of the answer section may have
         *                 multiple owner names because of aliases.  The AA bit
         */
        byteBuffer.putShort(id);
        byteBuffer.putShort(flags);
        byteBuffer.putShort(qdcount);
        byteBuffer.putShort(ancount);
        byteBuffer.putShort(nscount);
        byteBuffer.putShort(arcount);
    }
    public void writeQuestion(ByteBuffer byteBuffer){

        /*
         * 4.1.2. Question section format
         *
         * The question section is used to carry the "question" in most queries,
         * i.e., the parameters that define what is being asked.  The section
         * contains QDCOUNT (usually 1) entries, each of the following format:
         *
         *                                     1  1  1  1  1  1
         *       0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
         *     +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
         *     |                                               |
         *     /                     QNAME                     /
         *     /                                               /
         *     +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
         *     |                     QTYPE                     |
         *     +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
         *     |                     QCLASS                    |
         *     +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
         *
         * where:
         *
         * QNAME           a domain name represented as a sequence of labels, where
         *                 each label consists of a length octet followed by that
         *                 number of octets.  The domain name terminates with the
         *                 zero length octet for the null label of the root.  Note
         *                 that this field may be an odd number of octets; no
         *                 padding is used.
         *
         * QTYPE           a two octet code which specifies the type of the query.
         *                 The values for this field include all codes valid for a
         *                 TYPE field, together with some more general codes which
         *                 can match more than one type of RR.
         */


        byteBuffer.put(encodeDomainName("google.com"));
        byteBuffer.putShort((short) 0);
        byteBuffer.putShort((short) 0);
    }
    public void writeAnswer(ByteBuffer byteBuffer){
        byteBuffer.put(encodeDomainName("google.com"));
        byteBuffer.putShort((short) 1);
        byteBuffer.putShort((short) 1);
        byteBuffer.putInt(234);
        byteBuffer.put(new byte[] {8,8,8,8});
    }

}
