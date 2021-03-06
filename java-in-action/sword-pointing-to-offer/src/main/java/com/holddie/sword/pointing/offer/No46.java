package com.holddie.sword.pointing.offer;

/**
 * 把数字翻译成字符串
 * @author yangze1
 * @version 1.0.0
 * @email holddie@163.com
 * @date 2018/6/9 14:38
 */
public class No46 {

    private int numDecodings(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        int n = s.length();
        int[] dp = new int[n + 1];
        dp[0] = 1;
        dp[1] = s.charAt(0) == '0' ? 0 : 1;
        for (int i = 2; i <= n; i++) {
            int one = Integer.valueOf(s.substring(i - 1, i));
            if (one != 0) {
                dp[i] += dp[i - 1];
            }
            if (s.charAt(i - 2) == '0') {
                continue;
            }
            int two = Integer.valueOf(s.substring(i - 2, i));
            if (two <= 26) {
                dp[i] += dp[i - 2];
            }
        }
        return dp[n];
    }


    public static void main(String[] args) {
        No46 no46 = new No46();
        System.out.println(no46.numDecodings("122"));
    }

}
