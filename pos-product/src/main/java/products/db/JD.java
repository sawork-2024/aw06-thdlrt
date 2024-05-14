package com.example.webpos.db;

import com.example.webpos.model.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JD implements PosDB {


    private List<Product> products = null;

    @Override
    @Cacheable(value = "productsCache")
    public List<Product> getProducts() {
        try {
            if (products == null)
                products = parseJD("Java");
        } catch (IOException e) {
            products = new ArrayList<>();
        }
        return products;
    }

    @Override
    @Cacheable(value = "productCache")
    public Product getProduct(String productId) {
        for (Product p : getProducts()) {
            if (p.getId().toString().equals(productId)) {
                return p;
            }
        }
        return null;
    }

    public static List<Product> parseJD(String keyword) throws IOException {
        //获取请求https://search.jd.com/Search?keyword=java
        String url = "https://search.jd.com/Search?keyword=" + keyword;
        String cookieString = "__jdu=17039456808531229929257; shshshfpa=b5fecc89-be48-ce5c-ad4a-9e6514ce5cc8-1709466803; shshshfpx=b5fecc89-be48-ce5c-ad4a-9e6514ce5cc8-1709466803; mba_muid=17039456808531229929257; pinId=5b5QbWBu2HYqJEJiGLwsbw; amp_adc4c4=7ZyqJYCreUsFH9HAHqPZDa.V3l3Sjk1S1FWb1JNbE5QUXdWM0k2TTlHTDFDMw==..1hpgqk5f4.1hpgqk5f4.0.a.a; __jdv=95931165%7Cdirect%7C-%7Cnone%7C-%7C1714467395874; mba_sid=17144673958783113877499392863.1; wlfstk_smdl=p63hageyjrjdojjxspsyilv206mty1en; TrackID=1p5fboMlEDagR-OytS9daOM3Lnf_1IQlEflKCalECluNVyGBAlA8qU8MM1lBUyE1MpBSOjFlUgf2snpcbVoU6A-CqOJsdcr4UfBR5vALrkHJDjE99jM2cZfuhUTheONLj; thor=C8F8A1D43A80CD695A801C12D3531200C9FBA49F3ABB5B2ECA6C76722F7B180625ACA5A0DACE4E5A3312A6BFDBE3075F257D38932F0545F37F7BD7C7079863A83EA7959D05AD6FAA4B2A5C16C0B984920F79C4C981F6EC803B7F70DBA2CA02492602C24E73CC7C74B1A219D9AC107ACA1CBE271BED485954384F4505A3DAFBA328806CF29AF01C50847FB9E1FAA9AC7B; flash=2_gwLaxPDVDcWDHCFTnM_imD_xJ3sZ3MG-jbdYlBwHFoTxF_4TFLnqQJ9-sVP7Ll_Kh80W_i7Lhm8LXXsv6YtpjQv5SuIJQceCc5dDCwua9YXOU4-nwX8YKaX7wvi1DzCCEc7FIJALN2DQtJBNVTsmQRqYAnvIZCRb9uZBWG-q-Tj*; pin=18609824539_p; unick=lrtlrtthd; ceshi3.com=000; _tp=M6DTe65avUSYihBrni06dA%3D%3D; _pst=18609824539_p; ipLoc-djd=8-560-0-0; jsavif=1; __jda=143920055.17039456808531229929257.1703945681.1710064460.1714467395.4; __jdc=143920055; shshshfpb=BApXc6pMxLepACGKZTA8g6B-D9xKvCO0SBksHbyxo9xJ1Mp34Q4O2; areaId=8; 3AB9D23F7A4B3CSS=jdd03P3TOKOWRCFUWGXVOZZYLRTK5SBANGNNZPJA44OQ4W5A3WWR4AVWDX4BQSNRRYRFHPBM5YKA4PUKBDEQCZPNNE436ZQAAAAMPFZC3H4YAAAAACV7S4PSOWARVQQX; _gia_d=1; 3AB9D23F7A4B3C9B=P3TOKOWRCFUWGXVOZZYLRTK5SBANGNNZPJA44OQ4W5A3WWR4AVWDX4BQSNRRYRFHPBM5YKA4PUKBDEQCZPNNE436ZQ; __jdb=143920055.6.17039456808531229929257|4.1714467395";
        Map<String, String> cookies = parseCookies(cookieString);
        //解析网页
        Document document = Jsoup.connect(url).cookies(cookies).get();

        //所有js的方法都能用
        Element element = document.getElementById("J_goodsList");
        //获取所有li标签
        Elements elements = element.getElementsByTag("li");
//        System.out.println(element.html());
        List<Product> list = new ArrayList<>();

        //获取元素的内容
        for (Element el : elements
        ) {
            //关于图片特别多的网站，所有图片都是延迟加载的
            String id = el.attr("data-spu");
            String img = "https:".concat(el.getElementsByTag("img").eq(0).attr("data-lazy-img"));
            String price = el.getElementsByAttribute("data-price").text();
            String title = el.getElementsByClass("p-name").eq(0).text();
            if (title.indexOf("，") >= 0)
                title = title.substring(0, title.indexOf("，"));
            Product product;
            try{
                product = new Product(id, title, Double.parseDouble(price), img);
            }   catch (Exception e){
                continue;
            }
            list.add(product);
        }
        return list;
    }
    public static Map<String, String> parseCookies(String cookieString) {
        Map<String, String> cookies = new HashMap<>();
        String[] cookiePairs = cookieString.split(";\\s*");
        for (String cookiePair : cookiePairs) {
            String[] keyValue = cookiePair.split("=", 2);
            if (keyValue.length == 2) {
                cookies.put(keyValue[0], keyValue[1]);
            }
        }
        return cookies;
    }
}
