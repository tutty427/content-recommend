package org.recommend.www.service.impl;

import com.google.common.collect.Sets;
import org.recommend.www.api.ItemRecommendService;
import org.recommend.www.constants.Constants;
import org.recommend.www.dto.ItemSimiScore;
import org.recommend.www.dto.TitleSimiScore;
import org.recommend.www.mapper.db.recommend.ItemSimiScoreMapper;
import org.recommend.www.mapper.db.recommend.TitleSimiScoreMapper;
import org.recommend.www.model.SimilarItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * Created by shawxy on 8/9/16.
 * 1. 当都推不出的时候  是否加入随即模式
 * 2. 代码优化重构
 *
 */
public class ItemRecommendServiceImpl implements ItemRecommendService {

    @Autowired
    private ItemSimiScoreMapper itemSimiScoreMapper;
    @Autowired
    private TitleSimiScoreMapper titleSimiScoreMapper;

    public Set<SimilarItem> recommedItem(String source) {

        Set<SimilarItem> result = Sets.newHashSet();

        List<ItemSimiScore> simiItems =  itemSimiScoreMapper.selectBySourceId(source, Constants.RECOMMEND_COUNT);
        if(!CollectionUtils.isEmpty(simiItems) ){
            generateResult(result,simiItems,source);

        }

        if(result.size() == Constants.RECOMMEND_COUNT){
            return result;
        }

        //item-item 优先级更高，保留所有推荐内容 ，剩余用 title推导补充
        int itemSimilarCount = simiItems.size();
        Integer sourceId = getSourceId(source);

        List<TitleSimiScore> titleSimiItems = titleSimiScoreMapper.selectBySourceId(sourceId,Constants.RECOMMEND_COUNT);
        if(CollectionUtils.isEmpty(titleSimiItems)){
            return result;
        }

        int titleSimiItems_size = titleSimiItems.size();
        for(int i= 0 ; i < Constants.RECOMMEND_COUNT-itemSimilarCount; i++){

            if(titleSimiItems_size == i){break;}

            TitleSimiScore titleSimiScore = titleSimiItems.get(i);
            generateResultByTitle(result,titleSimiScore,source);

        }

        return result;
    }



    private void  generateResult(Set<SimilarItem> target, List<ItemSimiScore> source, String sourceId){


        for(ItemSimiScore itemSimiScore : source){

            SimilarItem similarItem = new SimilarItem();

            if(itemSimiScore.getItemId().equals(sourceId)){
                similarItem.setTargetItemId(itemSimiScore.getTargetItemId());
            }else{
                similarItem.setTargetItemId(itemSimiScore.getItemId());
            }

            similarItem.setScore(itemSimiScore.getScore());
            target.add(similarItem);
        }

    }


    private void  generateResultByTitle(Set<SimilarItem> target, TitleSimiScore source, String sourceId){


            SimilarItem similarItem = new SimilarItem();

            if(source.getTitleIdA().equals(sourceId)){
                similarItem.setTargetItemId(source.getTitleIdB());
            }else{
                similarItem.setTargetItemId(source.getTitleIdA());
            }

            similarItem.setScore(source.getScore());
            target.add(similarItem);

    }



    private Integer getSourceId(String source){


        int last_ = source.lastIndexOf('_');
        String id_string = source.substring(last_);

        return Integer.valueOf(id_string);

    }
}
