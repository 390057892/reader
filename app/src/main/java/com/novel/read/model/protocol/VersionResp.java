package com.novel.read.model.protocol;

/**
 * create by 赵利君 on 2019/6/25
 * describe:
 */
public class VersionResp {

    /**
     * version : {"id":2,"version":"1.0.0","size":"5096","content":"正式上线","download":"https://play.google.com/store/apps/details?id=com.freebook.bookreader","coerce":1,"status":1,"push_time":1560060702,"create_time":1560060772,"update_time":1560752842,"delete_time":null}
     */

    private VersionBean version;

    public VersionBean getVersion() {
        return version;
    }

    public void setVersion(VersionBean version) {
        this.version = version;
    }

    public static class VersionBean {
        /**
         * id : 2
         * version : 1.0.0
         * size : 5096
         * content : 正式上线
         * download : https://play.google.com/store/apps/details?id=com.freebook.bookreader
         * coerce : 1
         * status : 1
         * push_time : 1560060702
         * create_time : 1560060772
         * update_time : 1560752842
         * delete_time : null
         */

        private int id;
        private String version;
        private String size;
        private String content;
        private String download;
        private int coerce;
        private int status;
        private int push_time;
        private int create_time;
        private int update_time;
        private Object delete_time;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getDownload() {
            return download;
        }

        public void setDownload(String download) {
            this.download = download;
        }

        public int getCoerce() {
            return coerce;
        }

        public void setCoerce(int coerce) {
            this.coerce = coerce;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getPush_time() {
            return push_time;
        }

        public void setPush_time(int push_time) {
            this.push_time = push_time;
        }

        public int getCreate_time() {
            return create_time;
        }

        public void setCreate_time(int create_time) {
            this.create_time = create_time;
        }

        public int getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(int update_time) {
            this.update_time = update_time;
        }

        public Object getDelete_time() {
            return delete_time;
        }

        public void setDelete_time(Object delete_time) {
            this.delete_time = delete_time;
        }
    }
}
