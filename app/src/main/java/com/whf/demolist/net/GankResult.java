package com.whf.demolist.net;

import java.util.List;

/**
 * Created by @author WangHaoFei on 2017/11/13.
 */

public class GankResult {

    private boolean error;
    private List<GankEntry> results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<GankEntry> getResults() {
        return results;
    }

    public void setResults(List<GankEntry> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "GankResult{" +
                "erro=" + error +
                ", results=" + results +
                '}';
    }

}
