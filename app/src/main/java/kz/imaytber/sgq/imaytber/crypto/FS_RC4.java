package kz.imaytber.sgq.imaytber.crypto;

/**
 * Created by fromsi on 18.02.18.
 */

public class FS_RC4 {
    private int[] C;
    private int[] K;
    private int[] KEY;
    private int[] CONTENT;
    private int swapI = 0;
    private int swapJ = 0;
    private int max;

    public FS_RC4(String KEY, String CONTENT) {
        max = Math.max(CONTENT.length(),KEY.length());
        C = new int[max];
        K = new int[max];
        this.KEY = new int[max];
        this.CONTENT = new int[max];
        init(KEY, CONTENT);
        generates();
        preparation();
    }

    public String start(){
        StringBuilder content = new StringBuilder();
        int symbol;
        for (int i = 0; i < this.KEY.length; i++) {
            symbol = this.KEY[i] ^ this.CONTENT[i];
            content.append((char)symbol);
        }
        return content.toString();
    }

    private void init(String KEY, String CONTENT){
        for (int i = 0; i < max; i++) {
            C[i] = i;
            K[i] = KEY.charAt(i % KEY.length());
            if (CONTENT.length() > i) this.CONTENT[i] = CONTENT.charAt(i);
        }
    }

    private void generates(){
        int j = 0;
        for (int i = 0; i < max; i++) {
            j = (j + C[i] + K[i]) % max;
            swapI = i;
            swapJ = j;
            C[swapJ] = swapI;
            C[swapI] = swapJ;
        }
    }

    private void preparation(){
        int j = 0;
        int m = 0;
        for (int i = 0; i < max; i++) {
            m = (m + 1) % max;
            j = (j + C[m]) % max;
            swapI = m;
            swapJ = j;
            C[swapJ] = swapI;
            C[swapI] = swapJ;
            this.KEY[i] = C[(C[m] + C[j]) % max];
        }
    }
}
