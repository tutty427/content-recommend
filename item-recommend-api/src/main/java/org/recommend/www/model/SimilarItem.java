package org.recommend.www.model;

import java.io.Serializable;
import lombok.Data;
/**
 * Created by shawxy on 8/10/16.
 */

@Data
public class SimilarItem implements Serializable, Comparable<SimilarItem> {


    private String targetItemId;
    private Double score;


    public int compareTo(SimilarItem o) {

        if(o.getScore() > this.getScore()){
            return 1;
        }else if(o.getScore() < this.getScore()){
            return -1;
        }else{
            return 0;
        }
    }
}
