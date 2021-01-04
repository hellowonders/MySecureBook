package com.qdt.blockchain.support.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2018/5/16.
 *
 * @author piaozaiguang
 * @since 1.0
 */
public class MerkleRootUtil {

    public static String getMerkleRoot(List<String> allData) {
        int count = allData.size();

        List<String> previousTreeLayer = allData;
        List<String> treeLayer = allData;

        while (count > 1) {
            treeLayer = new ArrayList<>();
            for (int i = 1; i < allData.size(); i += 2) {
                treeLayer.add(HashUtil.applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }

        return  (treeLayer.size() == 1) ? treeLayer.get(0) : "";
    }

}
