package com.novel.read.model.protocol

import com.novel.read.utlis.StringUtils

import java.io.Serializable
import java.util.ArrayList

class SearchResp : Serializable {


    /**
     * book : [{"id":92,"title":"极品全能狂少","category_id":3,"cover":"http://dev.duoduvip.com/uploads/20190610/64512ebcd7e79923bc59ffc1d3afb597.png","description":"","hot":99437,"like":41,"author":"一支烟的快感","create_time":1560159443},{"id":170,"title":"抬棺匠","category_id":1,"cover":"http://dev.duoduvip.com/uploads/20190611/19cfa1ff96a4bec66b14e16b997ffba9.png","description":"","hot":99184,"like":54,"author":"陈八仙","create_time":1560205316},{"id":47,"title":"都市超级医圣","category_id":3,"cover":"http://dev.duoduvip.com/uploads/20190610/868b69fdf75dc303ca70fef4ccc8bc87.png","description":"    财法侣地，修行其实是一项非常耗钱的奢侈运动。无意中得到传说中道教学家，炼丹家，医药家葛洪的传承，淳朴的山里人葛东旭开始努力赚钱。当大多数人还在读高中时，他为了炼丹修行已经开始一边读书一边赚钱。当大多数人读大学还在为交女朋友的开销愁时，他已经是一名老板。当大多数人大学毕业在为找工作四处投简历，当富二代开着跑车，在美女面前炫耀时，他已经是一名级富一代，当然还是一名大隐隐于市的至尊医圣。<br></br>    各位书友要是觉得《都市超级医圣》还不错的话请不要忘记向您QQ群和微博里的朋友推荐哦！都市超级医圣最新章节,都市超级医圣无弹窗,都市超级医圣全文阅读.","hot":99056,"like":21,"author":"断桥残雪","create_time":1560129236},{"id":105,"title":"神医高手在都市","category_id":3,"cover":"http://dev.duoduvip.com/uploads/20190610/ccab1913cdaf9aee2cfeaef84910a700.png","description":"","hot":98517,"like":28,"author":"复仇","create_time":1560167691},{"id":21,"title":"夜少的心尖宝贝","category_id":1,"cover":"http://dev.duoduvip.com/uploads/20190609/c0f5c2ff7fa532e4bd399625e5edacf3.png","description":"    简介：<br></br>    为了能正大光明的睡乔小姐，夜少不惜采用了108种追妻方式结果有两个小萝卜头冒了出来，对他说，\u201c帅叔叔，你想追我妈咪吗我们可以教你哦\u201d夜少掀桌，\u201c叫什么叔叔乖，叫爹地\u201d夜少一生最风光得意的事情就是睡了乔小姐，留下了种，让她给自己生了两个宝贝儿子，在她的身上永恒的打上了自己的烙印夜少漫漫追妻之旅，甜甜甜，超甜宠文，1v1<br></br>    各位书友要是觉得《夜少的心尖宝贝》还不错的话请不要忘记向您QQ群和微博里的朋友推荐哦！夜少的心尖宝贝最新章节,夜少的心尖宝贝无弹窗,夜少的心尖宝贝全文阅读.","hot":97296,"like":64,"author":"慕欢颜","create_time":1560095819},{"id":102,"title":"万域灵神","category_id":4,"cover":"http://dev.duoduvip.com/uploads/20190610/847703596e2af187311bb9f5ab7c26b4.png","description":"","hot":97157,"like":56,"author":"乾多多","create_time":1560165904},{"id":87,"title":"至尊剑皇","category_id":4,"cover":"http://dev.duoduvip.com/uploads/20190610/711a87b0df3446885739845fba33fce4.png","description":"","hot":96684,"like":23,"author":"半步沧桑","create_time":1560156886},{"id":166,"title":"生活在美利坚的森林游侠","category_id":3,"cover":"http://dev.duoduvip.com/uploads/20190611/d663219f43a8bbba3d0718637fa0724a.png","description":"","hot":96427,"like":77,"author":"酱疙瘩","create_time":1560203771},{"id":171,"title":"三界红包群","category_id":3,"cover":"http://dev.duoduvip.com/uploads/nocover.jpg","description":"","hot":96362,"like":47,"author":"小教主","create_time":1560205922},{"id":131,"title":"命之途","category_id":2,"cover":"http://dev.duoduvip.com/uploads/nocover.jpg","description":"","hot":94835,"like":27,"author":"莫若梦兮","create_time":1560186387}]
     * limit : 10
     */

    var limit: Int = 0
    var book: List<BookBean> = ArrayList()

    class BookBean {
        /**
         * id : 92
         * title : 极品全能狂少
         * category_id : 3
         * cover : http://dev.duoduvip.com/uploads/20190610/64512ebcd7e79923bc59ffc1d3afb597.png
         * description :
         * hot : 99437
         * like : 41
         * author : 一支烟的快感
         * create_time : 1560159443
         */

        var id: Int = 0
        var title: String? = null
            get() = if (field == null) "" else StringUtils.convertCC(field!!)
        var category_id: Int = 0
        var cover: String? = null
            get() = if (field == null) "" else field
        var description: String? = null
            get() = if (field == null) "" else StringUtils.convertCC(StringUtils.delete160(field!!))
        var hot: Int = 0
        var like: Int = 0
        var author: String? = null
            get() = if (field == null) "" else StringUtils.convertCC(field!!)
        var create_time: Int = 0
    }
}
