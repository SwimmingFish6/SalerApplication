package com.example.fruit.salerapplication.commontool;

import com.example.fruit.salerapplication.testhttpapi.bean.FruitTypeBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by fruit on 2017/7/14.
 */

public class FruitTypeInfo {
    public static String UNKNOWN_TYPE_NAME = "未知水果";
    public static Long UNKNOWN_PIC_ID = 0L;
    public static HashMap<Long, String> fruitTypeNameMap = null;
    public static HashMap<Long, Long> fruitTypePictureMap = null;

    public static void initialize(HashMap<Long, String> nameMap, HashMap<Long, Long> pictureMap){
        FruitTypeInfo.fruitTypeNameMap = nameMap;
        FruitTypeInfo.fruitTypePictureMap = pictureMap;
    }

    public static String getFruitTypeName(Long typeId){
        if(fruitTypeNameMap!=null && fruitTypeNameMap.containsKey(typeId)){
            return fruitTypeNameMap.get(typeId);
        }
        return UNKNOWN_TYPE_NAME;
    }

    public static Long getPictureId(Long typeId){
        if(fruitTypePictureMap!=null && fruitTypePictureMap.containsKey(typeId)){
            return fruitTypePictureMap.get(typeId);
        }
        return UNKNOWN_PIC_ID;
    }

}
