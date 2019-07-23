package com.yt.hz.financial.argame.bean;

import java.util.List;

public class AnswerBean {

    /**
     * globalId : 85351444940155001
     * intent : {"code":100000,"operateState":1010}
     * results : [{"groupType":0,"resultType":"text","values":{"emotionId":10300,"sentenceId":101,"text":"你好，你可终于来找我了。"}}]
     */

    private String globalId;
    private IntentBean intent;
    private List<ResultsBean> results;

    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public IntentBean getIntent() {
        return intent;
    }

    public void setIntent(IntentBean intent) {
        this.intent = intent;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class IntentBean {
        /**
         * code : 100000
         * operateState : 1010
         */

        private int code;
        private int operateState;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public int getOperateState() {
            return operateState;
        }

        public void setOperateState(int operateState) {
            this.operateState = operateState;
        }
    }

    public static class ResultsBean {
        /**
         * groupType : 0
         * resultType : text
         * values : {"emotionId":10300,"sentenceId":101,"text":"你好，你可终于来找我了。"}
         */

        private int groupType;
        private String resultType;
        private ValuesBean values;

        public int getGroupType() {
            return groupType;
        }

        public void setGroupType(int groupType) {
            this.groupType = groupType;
        }

        public String getResultType() {
            return resultType;
        }

        public void setResultType(String resultType) {
            this.resultType = resultType;
        }

        public ValuesBean getValues() {
            return values;
        }

        public void setValues(ValuesBean values) {
            this.values = values;
        }

        public static class ValuesBean {
            /**
             * emotionId : 10300
             * sentenceId : 101
             * text : 你好，你可终于来找我了。
             */

            private int emotionId;
            private int sentenceId;
            private String text;

            public int getEmotionId() {
                return emotionId;
            }

            public void setEmotionId(int emotionId) {
                this.emotionId = emotionId;
            }

            public int getSentenceId() {
                return sentenceId;
            }

            public void setSentenceId(int sentenceId) {
                this.sentenceId = sentenceId;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }
        }
    }
}
