package com.example;

import android.content.res.Resources;
import android.renderscript.Float4;
import android.renderscript.RenderScriptGL;

public class RenderExample {
    private RenderScriptGL mRs;
    private ScriptC_example mScript;
    
    public RenderExample(RenderScriptGL rs, Resources res, int resId) {
        mRs = rs;
        mScript = new ScriptC_example(rs, res, resId);
        mRs.bindRootScript(mScript);
    }
    
    public void setBackgroundColor(Float4 color) {
        mScript.set_bgColor(color);
    }
}
