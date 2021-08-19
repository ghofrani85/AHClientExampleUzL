package eu.arrowhead.client.skeleton.common.misc;

import eu.arrowhead.client.skeleton.common.Constants;
import eu.arrowhead.common.Utilities;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Contains various methods that are required for operation or debugging
 */
public class Miscellaneous {
    /**
     * print objects in pretty JSON to console
     * @param object
     */
    public static void printOut(Object object) {
        System.out.println(Utilities.toPrettyJson(Utilities.toJson(object)));
    }

    /**
     * encode a given string based on a hash algorithm (given in Constants)
     * @param data
     * @return
     */
    public static String generateHandle(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance(Constants.HASH_ALGORITHM);
            byte[] messageDigest = md.digest(data.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);

            while (hashtext.length() < 32)
                hashtext = "0" + hashtext;

            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * return all interface addresses from all NICs of the client
     * @return
     * @throws SocketException
     */
    public static List<InetAddress> getInetAddressesFromNics() throws SocketException {
        List<InetAddress> addrList = new ArrayList<InetAddress>();

        // iterate through all NIC interfaces
        for(Enumeration<NetworkInterface> eni = NetworkInterface.getNetworkInterfaces(); eni.hasMoreElements(); ) {
            final NetworkInterface ifc = eni.nextElement();

            // ignore loopback and down interfaces
            if(ifc.isUp() && !ifc.isLoopback()) {
                // iterate throw all addresses of interfaces
                for (Enumeration<InetAddress> ena = ifc.getInetAddresses(); ena.hasMoreElements(); ) {
                    InetAddress addr = ena.nextElement();

                    // ignore linklocal and 169.x.x.x addresses
                    if (!addr.isLinkLocalAddress() && !addr.getHostAddress().matches("169.[0-9]+.[0-9]+.[0-9]+"))
                        addrList.add(addr);
                }
            }
        }

        return addrList;
    }
}
