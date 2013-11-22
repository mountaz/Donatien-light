package donatien.util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;

	/**
	 * Class declaration
	 *
	 *
	 * @version 1.0.0.1
	 */
	public class StringConverter {
		private static HashMap<String,String> iso = new HashMap<String , String>() {{
			put("&quot","&#34");
			put("&amp","&#38");
			put("&euro","&#128");
			put("&lt","&#139");
			put("&gt","&#155");
			put("&oelig","&#156");
			put("&oelig","&#156");
			put("&Yuml","&#159");
			put("&nbsp","&#160");
			put("&iexcl","&#161");
			put("&cent","&#162");
			put("&pound","&#163");
			put("&curren","&#164");
			put("&yen","&#165");
			put("&brvbar","&#166");
			put("&sect","&#167");
			put("&uml","&#168");
			put("&copy","&#169");
			put("&ordf","&#170");
			put("&laquo","&#171");
			put("&not","&#172");
			put("&shy","&#173");
			put("&reg","&#174");
			put("&masr","&#175");
			put("&deg","&#176");
			put("&plusmn","&#177");
			put("&sup2","&#178");
			put("&sup3","&#179");
			put("&acute","&#180");
			put("&micro","&#181");
			put("&para","&#182");
			put("&middot","&#183");
			put("&cedil","&#184");
			put("&sup1","&#185");
			put("&ordm","&#186");
			put("&raquo","&#187");
			put("&frac14","&#188");
			put("&frac12","&#189");
			put("&frac34","&#190");
			put("&iquest","&#191");
			put("&Agrave","&#192");
			put("&Aacute","&#193");
			put("&Acirc","&#194");
			put("&Atilde","&#195");
			put("&Auml","&#196");
			put("&Aring","&#197");
			put("&Aelig","&#198");
			put("&Ccedil","&#199");
			put("&Egrave","&#200");
			put("&Eacute","&#201");
			put("&Ecirc","&#202");
			put("&Euml","&#203");
			put("&Igrave","&#204");
			put("&Iacute","&#205");
			put("&Icirc","&#206");
			put("&Iuml","&#207");
			put("&eth","&#208");
			put("&Ntilde","&#209");
			put("&Ograve","&#210");
			put("&Oacute","&#211");
			put("&Ocirc","&#212");
			put("&Otilde","&#213");
			put("&Ouml","&#214");
			put("&times","&#215");
			put("&Oslash","&#216");
			put("&Ugrave","&#217");
			put("&Uacute","&#218");
			put("&Ucirc","&#219");
			put("&Uuml","&#220");
			put("&Yacute","&#221");
			put("&thorn","&#222");
			put("&szlig","&#223");
			put("&agrave","&#224");
			put("&aacute","&#225");
			put("&acirc","&#226");
			put("&atilde","&#227");
			put("&auml","&#228");
			put("&aring","&#229");
			put("&aelig","&#230");
			put("&ccedil","&#231");
			put("&egrave","&#232");
			put("&eacute","&#233");
			put("&ecirc","&#234");
			put("&euml","&#235");
			put("&igrave","&#236");
			put("&iacute","&#237");
			put("&icirc","&#238");
			put("&iuml","&#239");
			put("&eth","&#240");
			put("&ntilde","&#241");
			put("&ograve","&#242");
			put("&oacute","&#243");
			put("&ocirc","&#244");
			put("&otilde","&#245");
			put("&ouml","&#246");
			put("&divide","&#247");
			put("&oslash","&#248");
			put("&ugrave","&#249");
			put("&uacute","&#250");
			put("&ucirc","&#251");
			put("&uuml","&#252");
			put("&yacute","&#253");
			put("&thorn","&#254");
			put("&yuml","&#255");
		}};
//table for conversion from HTML code to iso
	    private static final char   HEXCHAR[] = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
		'e', 'f'
	    };
	    private static final String HEXINDEX = "0123456789abcdef          ABCDEF";

	    /**
	     * Method declaration
	     *
	     *
	     * @param s
	     *
	     * @return
	     */
	    public static byte[] hexToByte(String s) {
		int  l = s.length() / 2;
		byte data[] = new byte[l];
		int  j = 0;

		for (int i = 0; i < l; i++) {
		    char c = s.charAt(j++);
		    int  n, b;

		    n = HEXINDEX.indexOf(c);
		    b = (n & 0xf) << 4;
		    c = s.charAt(j++);
		    n = HEXINDEX.indexOf(c);
		    b += (n & 0xf);
		    data[i] = (byte) b;
		}

		return data;
	    }

	    /**
	     * Method declaration
	     *
	     *
	     * @param b
	     *
	     * @return
	     */
	    static String byteToHex(byte b[]) {
		int	     len = b.length;
		StringBuffer s = new StringBuffer();

		for (int i = 0; i < len; i++) {
		    int c = ((int) b[i]) & 0xff;

		    s.append(HEXCHAR[c >> 4 & 0xf]);
		    s.append(HEXCHAR[c & 0xf]);
		}

		return s.toString();
	    }
	    
		public static String convertEntitiesToIso(String raw){
		 String result="";
		 Object[] tab = iso.keySet().toArray();
		 for (int i=0;i<tab.length;i++)
		 {
//				System.out.println("replace "+tab[i].toString()+" by "+iso.get(tab[i]));
//				System.out.println("avant "+tab[i].toString()+" by "+iso.get(tab[i]));
						 raw = raw.replaceAll(tab[i].toString(),iso.get(tab[i]));
		 }
		 return raw;
		}
	    /**
	     * Method declaration
	     *
	     *
	     * @param s
	     *
	     * @return
	     */
	    static String unicodeToHexString(String s) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream      out = new DataOutputStream(bout);

		try {
		    out.writeUTF(s);
		    out.close();
		    bout.close();
		} catch (IOException e) {
		    return null;
		}

		return byteToHex(bout.toByteArray());
	    }

	    /**
	     * Method declaration
	     *
	     *
	     * @param s
	     *
	     * @return
	     */
	    public static String hexStringToUnicode(String s) {
		byte[]		     b = hexToByte(s);
		ByteArrayInputStream bin = new ByteArrayInputStream(b);
		DataInputStream      in = new DataInputStream(bin);

		try {
		    return in.readUTF();
		} catch (IOException e) {
		    return null;
		}
	    }

	    /**
	     * Method declaration
	     *
	     *
	     * @param s
	     *
	     * @return
	     */
	    public static String unicodeToAscii(String s) {
		if (s == null || s.equals("")) {
		    return s;
		}

		int	     len = s.length();
		StringBuffer b = new StringBuffer(len);

		for (int i = 0; i < len; i++) {
		    char c = s.charAt(i);

		    if (c == '\\') {
			if (i < len - 1 && s.charAt(i + 1) == 'u') {
			    b.append(c);    // encode the \ as unicode, so 'u' is ignored
			    b.append("u005c");    // splited so the source code is not changed...
			} else {
			    b.append(c);
			}
		    } else if ((c >= 0x0020) && (c <= 0x007f)) {
			b.append(c);    // this is 99%
		    } else {
			b.append("\\u");
			b.append(HEXCHAR[(c >> 12) & 0xf]);
			b.append(HEXCHAR[(c >> 8) & 0xf]);
			b.append(HEXCHAR[(c >> 4) & 0xf]);
			b.append(HEXCHAR[c & 0xf]);
		    }
		}

		return b.toString();
	    }

	    /**
	     * Method declaration
	     *
	     *
	     * @param s
	     *
	     * @return
	     */
	    public static String asciiToUnicode(String s) {
		if (s == null || s.indexOf("\\u") == -1) {
		    return s;
		}

		int  len = s.length();
		char b[] = new char[len];
		int  j = 0;

		for (int i = 0; i < len; i++) {
		    char c = s.charAt(i);

		    if (c != '\\' || i == len - 1) {
			b[j++] = c;
		    } else {
			c = s.charAt(++i);

			if (c != 'u' || i == len - 1) {
			    b[j++] = '\\';
			    b[j++] = c;
			} else {
			    int k = (HEXINDEX.indexOf(s.charAt(++i)) & 0xf) << 12;

			    k += (HEXINDEX.indexOf(s.charAt(++i)) & 0xf) << 8;
			    k += (HEXINDEX.indexOf(s.charAt(++i)) & 0xf) << 4;
			    k += (HEXINDEX.indexOf(s.charAt(++i)) & 0xf);
			    b[j++] = (char) k;
			}
		    }
		}

		return new String(b, 0, j);
	    }

	    /**
	     * Method declaration
	     *
	     *
	     * @param x
	     *
	     * @return
	     *
	     */
	    public static String InputStreamToString(InputStream x) {
		InputStreamReader in = new InputStreamReader(x);
		StringWriter      write = new StringWriter();
		int		  blocksize = 8 * 1024;    // todo: is this a good value?
		char		  buffer[] = new char[blocksize];

		try {
		    while (true) {
			int l = in.read(buffer, 0, blocksize);

			if (l == -1) {
			    break;
			}

			write.write(buffer, 0, l);
		    }

		    write.close();
		    x.close();
		} catch (IOException e) {
//		    throw Trace.error(Trace.INPUTSTREAM_ERROR, e.getMessage());
		}

		return write.toString();
	    }

	}

