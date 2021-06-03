package com.jfnice.qiniu;

/*
 * js版或c#版，见： http://hyhvi.iteye.com/blog/1923130
 */
public class DesUtil {

    public static String jsencrypt(String key, String message) {
        String ciphertext = stringToHex(des(key, message, 1, 0, "0"));
        return ciphertext;
    }

    public static String jsdecrypt(String key, String message) {
        String plaintext = des(key, HexTostring(message), 0, 0, "0");
        return plaintext;
    }

    private static String des(String key, String message, int encrypt, int mode, String iv) {
        int[] spfunction1 = new int[]{0x1010400, 0, 0x10000, 0x1010404, 0x1010004, 0x10404, 0x4, 0x10000, 0x400, 0x1010400, 0x1010404, 0x400, 0x1000404, 0x1010004, 0x1000000, 0x4, 0x404, 0x1000400, 0x1000400, 0x10400, 0x10400, 0x1010000, 0x1010000, 0x1000404, 0x10004, 0x1000004, 0x1000004, 0x10004, 0, 0x404, 0x10404, 0x1000000, 0x10000, 0x1010404, 0x4, 0x1010000, 0x1010400, 0x1000000, 0x1000000, 0x400, 0x1010004, 0x10000, 0x10400, 0x1000004, 0x400, 0x4, 0x1000404, 0x10404, 0x1010404, 0x10004, 0x1010000, 0x1000404, 0x1000004, 0x404, 0x10404, 0x1010400, 0x404, 0x1000400, 0x1000400, 0, 0x10004, 0x10400, 0, 0x1010004};
        int[] spfunction2 = new int[]{-0x7fef7fe0, -0x7fff8000, 0x8000, 0x108020, 0x100000, 0x20, -0x7fefffe0, -0x7fff7fe0, -0x7fffffe0, -0x7fef7fe0, -0x7fef8000, -0x8000000, -0x7fff8000, 0x100000, 0x20, -0x7fefffe0, 0x108000, 0x100020, -0x7fff7fe0, 0, -0x8000000, 0x8000, 0x108020, -0x7ff00000, 0x100020, -0x7fffffe0, 0, 0x108000, 0x8020, -0x7fef8000, -0x7ff00000, 0x8020, 0, 0x108020, -0x7fefffe0, 0x100000, -0x7fff7fe0, -0x7ff00000, -0x7fef8000, 0x8000, -0x7ff00000, -0x7fff8000, 0x20, -0x7fef7fe0, 0x108020, 0x20, 0x8000, -0x8000000, 0x8020, -0x7fef8000, 0x100000, -0x7fffffe0, 0x100020, -0x7fff7fe0, -0x7fffffe0, 0x100020, 0x108000, 0, -0x7fff8000, 0x8020, -0x8000000, -0x7fefffe0, -0x7fef7fe0, 0x108000};
        int[] spfunction3 = new int[]{0x208, 0x8020200, 0, 0x8020008, 0x8000200, 0, 0x20208, 0x8000200, 0x20008, 0x8000008, 0x8000008, 0x20000, 0x8020208, 0x20008, 0x8020000, 0x208, 0x8000000, 0x8, 0x8020200, 0x200, 0x20200, 0x8020000, 0x8020008, 0x20208, 0x8000208, 0x20200, 0x20000, 0x8000208, 0x8, 0x8020208, 0x200, 0x8000000, 0x8020200, 0x8000000, 0x20008, 0x208, 0x20000, 0x8020200, 0x8000200, 0, 0x200, 0x20008, 0x8020208, 0x8000200, 0x8000008, 0x200, 0, 0x8020008, 0x8000208, 0x20000, 0x8000000, 0x8020208, 0x8, 0x20208, 0x20200, 0x8000008, 0x8020000, 0x8000208, 0x208, 0x8020000, 0x20208, 0x8, 0x8020008, 0x20200};
        int[] spfunction4 = new int[]{0x802001, 0x2081, 0x2081, 0x80, 0x802080, 0x800081, 0x800001, 0x2001, 0, 0x802000, 0x802000, 0x802081, 0x81, 0, 0x800080, 0x800001, 0x1, 0x2000, 0x800000, 0x802001, 0x80, 0x800000, 0x2001, 0x2080, 0x800081, 0x1, 0x2080, 0x800080, 0x2000, 0x802080, 0x802081, 0x81, 0x800080, 0x800001, 0x802000, 0x802081, 0x81, 0, 0, 0x802000, 0x2080, 0x800080, 0x800081, 0x1, 0x802001, 0x2081, 0x2081, 0x80, 0x802081, 0x81, 0x1, 0x2000, 0x800001, 0x2001, 0x802080, 0x800081, 0x2001, 0x2080, 0x800000, 0x802001, 0x80, 0x800000, 0x2000, 0x802080};
        int[] spfunction5 = new int[]{0x100, 0x2080100, 0x2080000, 0x42000100, 0x80000, 0x100, 0x40000000, 0x2080000, 0x40080100, 0x80000, 0x2000100, 0x40080100, 0x42000100, 0x42080000, 0x80100, 0x40000000, 0x2000000, 0x40080000, 0x40080000, 0, 0x40000100, 0x42080100, 0x42080100, 0x2000100, 0x42080000, 0x40000100, 0, 0x42000000, 0x2080100, 0x2000000, 0x42000000, 0x80100, 0x80000, 0x42000100, 0x100, 0x2000000, 0x40000000, 0x2080000, 0x42000100, 0x40080100, 0x2000100, 0x40000000, 0x42080000, 0x2080100, 0x40080100, 0x100, 0x2000000, 0x42080000, 0x42080100, 0x80100, 0x42000000, 0x42080100, 0x2080000, 0, 0x40080000, 0x42000000, 0x80100, 0x2000100, 0x40000100, 0x80000, 0, 0x40080000, 0x2080100, 0x40000100};
        int[] spfunction6 = new int[]{0x20000010, 0x20400000, 0x4000, 0x20404010, 0x20400000, 0x10, 0x20404010, 0x400000, 0x20004000, 0x404010, 0x400000, 0x20000010, 0x400010, 0x20004000, 0x20000000, 0x4010, 0, 0x400010, 0x20004010, 0x4000, 0x404000, 0x20004010, 0x10, 0x20400010, 0x20400010, 0, 0x404010, 0x20404000, 0x4010, 0x404000, 0x20404000, 0x20000000, 0x20004000, 0x10, 0x20400010, 0x404000, 0x20404010, 0x400000, 0x4010, 0x20000010, 0x400000, 0x20004000, 0x20000000, 0x4010, 0x20000010, 0x20404010, 0x404000, 0x20400000, 0x404010, 0x20404000, 0, 0x20400010, 0x10, 0x4000, 0x20400000, 0x404010, 0x4000, 0x400010, 0x20004010, 0, 0x20404000, 0x20000000, 0x400010, 0x20004010};
        int[] spfunction7 = new int[]{0x200000, 0x4200002, 0x4000802, 0, 0x800, 0x4000802, 0x200802, 0x4200800, 0x4200802, 0x200000, 0, 0x4000002, 0x2, 0x4000000, 0x4200002, 0x802, 0x4000800, 0x200802, 0x200002, 0x4000800, 0x4000002, 0x4200000, 0x4200800, 0x200002, 0x4200000, 0x800, 0x802, 0x4200802, 0x200800, 0x2, 0x4000000, 0x200800, 0x4000000, 0x200800, 0x200000, 0x4000802, 0x4000802, 0x4200002, 0x4200002, 0x2, 0x200002, 0x4000000, 0x4000800, 0x200000, 0x4200800, 0x802, 0x200802, 0x4200800, 0x802, 0x4000002, 0x4200802, 0x4200000, 0x200800, 0, 0x2, 0x4200802, 0, 0x200802, 0x4200000, 0x800, 0x4000002, 0x4000800, 0x800, 0x200002};
        int[] spfunction8 = new int[]{0x10001040, 0x1000, 0x40000, 0x10041040, 0x10000000, 0x10001040, 0x40, 0x10000000, 0x40040, 0x10040000, 0x10041040, 0x41000, 0x10041000, 0x41040, 0x1000, 0x40, 0x10040000, 0x10000040, 0x10001000, 0x1040, 0x41000, 0x40040, 0x10040040, 0x10041000, 0x1040, 0, 0, 0x10040040, 0x10000040, 0x10001000, 0x41040, 0x40000, 0x41040, 0x40000, 0x10041000, 0x1000, 0x40, 0x10040040, 0x1000, 0x41040, 0x10001000, 0x40, 0x10000040, 0x10040000, 0x10040040, 0x10000000, 0x40000, 0x10001040, 0, 0x10041040, 0x40040, 0x10000040, 0x10040000, 0x10001000, 0x10001040, 0, 0x10041040, 0x41000, 0x41000, 0x1040, 0x1040, 0x40040, 0x10000000, 0x10041000};
        int[] keys = des_createKeys(key);
        int m = 0, i, j, temp, right1, right2, left, right;
        int[] looping;
        int cbcleft = 0, cbcleft2 = 0, cbcright = 0, cbcright2 = 0;
        int endloop, loopinc;
        int len = message.length();
        int chunk = 0;
        int iterations = keys.length == 32 ? 3 : 9;

        if (iterations == 3) {
            looping = encrypt != 0 ? new int[]{0, 32, 2} : new int[]{30, -2, -2};
        } else {
            looping = encrypt != 0 ? new int[]{0, 32, 2, 62, 30, -2, 64, 96, 2} : new int[]{94, 62, -2, 32, 64, 2, 30, -2, -2};
        }

        message += "\0\0\0\0\0\0\0\0";
        String result = "";
        String tempresult = "";

        if (mode == 1) {
            cbcleft = (iv.codePointAt(m++) << 24) | (iv.codePointAt(m++) << 16) | (iv.codePointAt(m++) << 8) | iv.codePointAt(m++);
            cbcright = (iv.codePointAt(m++) << 24) | (iv.codePointAt(m++) << 16) | (iv.codePointAt(m++) << 8) | iv.codePointAt(m++);
            m = 0;
        }

        while (m < len) {
            if (encrypt != 0) {
                left = (message.codePointAt(m++) << 16) | message.codePointAt(m++);
                right = (message.codePointAt(m++) << 16) | message.codePointAt(m++);
            } else {
                left = (message.codePointAt(m++) << 24) | (message.codePointAt(m++) << 16) | (message.codePointAt(m++) << 8) | message.codePointAt(m++);
                right = (message.codePointAt(m++) << 24) | (message.codePointAt(m++) << 16) | (message.codePointAt(m++) << 8) | message.codePointAt(m++);
            }

            if (mode == 1) {
                if (encrypt != 0) {
                    left ^= cbcleft;
                    right ^= cbcright;
                } else {
                    cbcleft2 = cbcleft;
                    cbcright2 = cbcright;
                    cbcleft = left;
                    cbcright = right;
                }
            }

            temp = ((left >>> 4) ^ right) & 0x0f0f0f0f;
            right ^= temp;
            left ^= (temp << 4);
            temp = ((left >>> 16) ^ right) & 0x0000ffff;
            right ^= temp;
            left ^= (temp << 16);
            temp = ((right >>> 2) ^ left) & 0x33333333;
            left ^= temp;
            right ^= (temp << 2);
            temp = ((right >>> 8) ^ left) & 0x00ff00ff;
            left ^= temp;
            right ^= (temp << 8);
            temp = ((left >>> 1) ^ right) & 0x55555555;
            right ^= temp;
            left ^= (temp << 1);
            left = ((left << 1) | (left >>> 31));
            right = ((right << 1) | (right >>> 31));
            for (j = 0; j < iterations; j += 3) {
                endloop = looping[j + 1];
                loopinc = looping[j + 2];
                for (i = looping[j]; i != endloop; i += loopinc) {
                    right1 = right ^ keys[i];
                    right2 = ((right >>> 4) | (right << 28)) ^ keys[i + 1];
                    temp = left;
                    left = right;
                    right = temp ^ (spfunction2[(right1 >>> 24) & 0x3f] | spfunction4[(right1 >>> 16) & 0x3f] | spfunction6[(right1 >>> 8) & 0x3f] | spfunction8[right1 & 0x3f] | spfunction1[(right2 >>> 24) & 0x3f] | spfunction3[(right2 >>> 16) & 0x3f] | spfunction5[(right2 >>> 8) & 0x3f] | spfunction7[right2 & 0x3f]);
                }

                temp = left;
                left = right;
                right = temp;
            }
            left = ((left >>> 1) | (left << 31));
            right = ((right >>> 1) | (right << 31));
            temp = ((left >>> 1) ^ right) & 0x55555555;
            right ^= temp;
            left ^= (temp << 1);
            temp = ((right >>> 8) ^ left) & 0x00ff00ff;
            left ^= temp;
            right ^= (temp << 8);
            temp = ((right >>> 2) ^ left) & 0x33333333;
            left ^= temp;
            right ^= (temp << 2);
            temp = ((left >>> 16) ^ right) & 0x0000ffff;
            right ^= temp;
            left ^= (temp << 16);
            temp = ((left >>> 4) ^ right) & 0x0f0f0f0f;
            right ^= temp;
            left ^= (temp << 4);
            if (mode == 1) {
                if (encrypt != 0) {
                    cbcleft = left;
                    cbcright = right;
                } else {
                    left ^= cbcleft2;
                    right ^= cbcright2;
                }
            }

            if (encrypt != 0) {
                tempresult += new String(new char[]{
                        (char) (left >>> 24), (char) ((left >>> 16) & 0xff), (char) ((left >>> 8) & 0xff), (char) (left & 0xff), (char) (right >>> 24), (char) ((right >>> 16) & 0xff), (char) ((right >>> 8) & 0xff), (char) (right & 0xff)
                });
                chunk += 16;
            } else {
                tempresult += new String(new char[]{
                        (char) ((left >>> 16) & 0xffff), (char) (left & 0xffff), (char) ((right >>> 16) & 0xffff), (char) (right & 0xffff)
                });
                chunk += 8;
            }

            if (chunk == 512) {
                result += tempresult;
                tempresult = "";
                chunk = 0;
            }
        }

        return result + tempresult;
    }

    private static int[] des_createKeys(String key) {
        int[] pc2bytes0 = new int[]{0, 0x4, 0x20000000, 0x20000004, 0x10000, 0x10004, 0x20010000, 0x20010004, 0x200, 0x204, 0x20000200, 0x20000204, 0x10200, 0x10204, 0x20010200, 0x20010204};
        int[] pc2bytes1 = new int[]{0, 0x1, 0x100000, 0x100001, 0x4000000, 0x4000001, 0x4100000, 0x4100001, 0x100, 0x101, 0x100100, 0x100101, 0x4000100, 0x4000101, 0x4100100, 0x4100101};
        int[] pc2bytes2 = new int[]{0, 0x8, 0x800, 0x808, 0x1000000, 0x1000008, 0x1000800, 0x1000808, 0, 0x8, 0x800, 0x808, 0x1000000, 0x1000008, 0x1000800, 0x1000808};
        int[] pc2bytes3 = new int[]{0, 0x200000, 0x8000000, 0x8200000, 0x2000, 0x202000, 0x8002000, 0x8202000, 0x20000, 0x220000, 0x8020000, 0x8220000, 0x22000, 0x222000, 0x8022000, 0x8222000};
        int[] pc2bytes4 = new int[]{0, 0x40000, 0x10, 0x40010, 0, 0x40000, 0x10, 0x40010, 0x1000, 0x41000, 0x1010, 0x41010, 0x1000, 0x41000, 0x1010, 0x41010};
        int[] pc2bytes5 = new int[]{0, 0x400, 0x20, 0x420, 0, 0x400, 0x20, 0x420, 0x2000000, 0x2000400, 0x2000020, 0x2000420, 0x2000000, 0x2000400, 0x2000020, 0x2000420};
        int[] pc2bytes6 = new int[]{0, 0x10000000, 0x80000, 0x10080000, 0x2, 0x10000002, 0x80002, 0x10080002, 0, 0x10000000, 0x80000, 0x10080000, 0x2, 0x10000002, 0x80002, 0x10080002};
        int[] pc2bytes7 = new int[]{0, 0x10000, 0x800, 0x10800, 0x20000000, 0x20010000, 0x20000800, 0x20010800, 0x20000, 0x30000, 0x20800, 0x30800, 0x20020000, 0x20030000, 0x20020800, 0x20030800};
        int[] pc2bytes8 = new int[]{0, 0x40000, 0, 0x40000, 0x2, 0x40002, 0x2, 0x40002, 0x2000000, 0x2040000, 0x2000000, 0x2040000, 0x2000002, 0x2040002, 0x2000002, 0x2040002};
        int[] pc2bytes9 = new int[]{0, 0x10000000, 0x8, 0x10000008, 0, 0x10000000, 0x8, 0x10000008, 0x400, 0x10000400, 0x408, 0x10000408, 0x400, 0x10000400, 0x408, 0x10000408};
        int[] pc2bytes10 = new int[]{0, 0x20, 0, 0x20, 0x100000, 0x100020, 0x100000, 0x100020, 0x2000, 0x2020, 0x2000, 0x2020, 0x102000, 0x102020, 0x102000, 0x102020};
        int[] pc2bytes11 = new int[]{0, 0x1000000, 0x200, 0x1000200, 0x200000, 0x1200000, 0x200200, 0x1200200, 0x4000000, 0x5000000, 0x4000200, 0x5000200, 0x4200000, 0x5200000, 0x4200200, 0x5200200};
        int[] pc2bytes12 = new int[]{0, 0x1000, 0x8000000, 0x8001000, 0x80000, 0x81000, 0x8080000, 0x8081000, 0x10, 0x1010, 0x8000010, 0x8001010, 0x80010, 0x81010, 0x8080010, 0x8081010};
        int[] pc2bytes13 = new int[]{0, 0x4, 0x100, 0x104, 0, 0x4, 0x100, 0x104, 0x1, 0x5, 0x101, 0x105, 0x1, 0x5, 0x101, 0x105};
        int iterations = key.length() >= 24 ? 3 : 1;
        int[] keys = new int[32 * iterations];
        int[] shifts = new int[]{0, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0};
        int lefttemp, righttemp, m = 0, n = 0, temp;

        for (int j = 0; j < iterations; j++) {

            int left1 = 0, left2 = 0, left3 = 0, left4 = 0;
            int right1 = 0, right2 = 0, right3 = 0, right4 = 0;

            try {
                left1 = key.codePointAt(m++) << 24;
            } catch (StringIndexOutOfBoundsException e) {
            }
            try {
                left2 = key.codePointAt(m++) << 16;
            } catch (StringIndexOutOfBoundsException e) {
            }
            try {
                left3 = key.codePointAt(m++) << 8;
            } catch (StringIndexOutOfBoundsException e) {
            }
            try {
                left4 = key.codePointAt(m++);
            } catch (StringIndexOutOfBoundsException e) {
            }

            try {
                right1 = key.codePointAt(m++) << 24;
            } catch (StringIndexOutOfBoundsException e) {
            }
            try {
                right2 = key.codePointAt(m++) << 16;
            } catch (StringIndexOutOfBoundsException e) {
            }
            try {
                right3 = key.codePointAt(m++) << 8;
            } catch (StringIndexOutOfBoundsException e) {
            }
            try {
                right4 = key.codePointAt(m++);
            } catch (StringIndexOutOfBoundsException e) {
            }

            int left = left1 | left2 | left3 | left4;
            int right = right1 | right2 | right3 | right4;

            temp = ((left >>> 4) ^ right) & 0x0f0f0f0f;
            right ^= temp;
            left ^= (temp << 4);
            temp = ((right >>> -16) ^ left) & 0x0000ffff;
            left ^= temp;
            right ^= (temp << -16);
            temp = ((left >>> 2) ^ right) & 0x33333333;
            right ^= temp;
            left ^= (temp << 2);
            temp = ((right >>> -16) ^ left) & 0x0000ffff;
            left ^= temp;
            right ^= (temp << -16);
            temp = ((left >>> 1) ^ right) & 0x55555555;
            right ^= temp;
            left ^= (temp << 1);
            temp = ((right >>> 8) ^ left) & 0x00ff00ff;
            left ^= temp;
            right ^= (temp << 8);
            temp = ((left >>> 1) ^ right) & 0x55555555;
            right ^= temp;
            left ^= (temp << 1);
            temp = (left << 8) | ((right >>> 20) & 0x000000f0);
            left = (right << 24) | ((right << 8) & 0xff0000) | ((right >>> 8) & 0xff00) | ((right >>> 24) & 0xf0);
            right = temp;

            for (int i = 0; i < shifts.length; i++) {
                if (shifts[i] != 0) {
                    left = (left << 2) | (left >>> 26);
                    right = (right << 2) | (right >>> 26);
                } else {
                    left = (left << 1) | (left >>> 27);
                    right = (right << 1) | (right >>> 27);
                }
                left &= -0xf;
                right &= -0xf;
                lefttemp = pc2bytes0[left >>> 28] | pc2bytes1[(left >>> 24) & 0xf] | pc2bytes2[(left >>> 20) & 0xf] | pc2bytes3[(left >>> 16) & 0xf] | pc2bytes4[(left >>> 12) & 0xf] | pc2bytes5[(left >>> 8) & 0xf] | pc2bytes6[(left >>> 4) & 0xf];
                righttemp = pc2bytes7[right >>> 28] | pc2bytes8[(right >>> 24) & 0xf] | pc2bytes9[(right >>> 20) & 0xf] | pc2bytes10[(right >>> 16) & 0xf] | pc2bytes11[(right >>> 12) & 0xf] | pc2bytes12[(right >>> 8) & 0xf] | pc2bytes13[(right >>> 4) & 0xf];
                temp = ((righttemp >>> 16) ^ lefttemp) & 0x0000ffff;
                keys[n++] = lefttemp ^ temp;
                keys[n++] = righttemp ^ (temp << 16);
            }
        }

        return keys;
    }

    private static String stringToHex(String s) {
        String r = "";
        String[] hexes = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        for (int i = 0; i < s.length(); i++) {
            r += hexes[s.codePointAt(i) >> 4] + hexes[s.codePointAt(i) & 0xf];
        }
        return r;
    }

    private static String HexTostring(String s) {
        String r = "";
        for (int i = 0; i < s.length(); i += 2) {
            int sxx = Integer.parseInt(s.substring(i, i + 2), 16);
            r += (char) sxx;
        }
        return r;
    }

}
