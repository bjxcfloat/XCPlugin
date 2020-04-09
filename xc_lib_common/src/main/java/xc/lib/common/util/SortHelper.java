package xc.lib.common.util;

import java.util.Arrays;

// 排序工具类
public class SortHelper {

    // 快速排序,升序  order:0 升序 1：降序
    public static void quickSort(Integer[] arr, int low, int high,int order){
        if(arr.length <= 0) return;
        if(low >= high) return;
        int left = low;
        int right = high;

        int temp = arr[left];   //挖坑1：保存基准的值
        while (left < right){
            while(left < right &&
                    ((order==0&&arr[right] >= temp)||(order==1&&arr[right] <= temp))){  //坑2：从后向前找到比基准小的元素，插入到基准位置坑1中
                right--;
            }
            arr[left] = arr[right];
            while(left < right &&
                    ((order==0&&arr[left] <= temp)||(order==1&&arr[left] >= temp))){   //坑3：从前往后找到比基准大的元素，放到刚才挖的坑2中
                left++;
            }
            arr[right] = arr[left];
        }
        arr[left] = temp;   //基准值填补到坑3中，准备分治递归快排
        quickSort(arr, low, left-1,order);
        quickSort(arr, left+1, high,order);
    }

    // 冒泡排序,较慢
    public static void bubleSort(Integer[] iArray, int order) {
        if (iArray.length <= 1) return;

        for (int i = 0; i < iArray.length - 1; i++)
            for (int j = i + 1; j < iArray.length; j++) {
                if ((order == 0&&iArray[i] > iArray[j] ) || ( order == 1&&iArray[i] < iArray[j])) {
                    int tmp = iArray[i];
                    iArray[i] = iArray[j];
                    iArray[j] = tmp;
                }
            }


    }

}
