package de.baumann.browser.browser

import de.baumann.browser.view.NinjaWebView

class WebContentPostProcessor {
    fun postProcess(ninjaWebView: NinjaWebView, url: String) {
        for (entry in urlScriptMap) {
            val entryUrl = entry.key
            val script = entry.value
            if (url.contains(entryUrl)) {
                ninjaWebView.evaluateJavascript(script, null)
            }
        }
    }

    companion object {
        private const val facebookHideSponsoredPostsJs = """
            javascript:(function() {
              var posts = [].filter.call(document.getElementsByTagName('article'), el => el.attributes['data-store'].value.indexOf('is_sponsored.1') >= 0); 
              while(posts.length > 0) { posts.pop().style.display = "none"; }
              
              var ads = Array.from(document.getElementsByClassName("bg-s3")).filter(e => e.innerText.indexOf("Sponsored") != -1);
              ads.forEach(el => {el.style.display="none"; el.nextSibling.style.display="none";el.nextSibling.nextSibling.style.display="none"});
              ads.forEach(el => {el.nextSibling.nextSibling.nextSibling.style.display="none"});
              ads.forEach(el => {el.nextSibling.nextSibling.nextSibling.nextSibling.style.display="none"});
            
            var qcleanObserver = new window.MutationObserver(function(mutation, observer){ 
              var posts = [].filter.call(document.getElementsByTagName('article'), el => el.attributes['data-store'].value.indexOf('is_sponsored.1') >= 0); 
              while(posts.length > 0) { posts.pop().style.display = "none"; }
              
              var ads = Array.from(document.getElementsByClassName("bg-s3")).filter(e => e.innerText.indexOf("Sponsored") != -1);
              ads.forEach(el => {el.style.display="none"; el.nextSibling.style.display="none";el.nextSibling.nextSibling.style.display="none"});
              ads.forEach(el => {el.nextSibling.nextSibling.nextSibling.style.display="none"});
              ads.forEach(el => {el.nextSibling.nextSibling.nextSibling.nextSibling.style.display="none"});
            });
            
            qcleanObserver.observe(document, { subtree: true, childList: true });
            })()
        """

        private const val zhihuDisablePopupJs = """
            document.querySelector(".ContentItem-expandButton").click();
            document.querySelector(".ModalWrap-item:last-child .ModalWrap-itemBtn").click();
            document.querySelector(".SkipModal .Button.Button--plain").click();
            document.querySelector("button.OpenInAppButton").remove();
        """

        private const val jianshuJs = """
            document.querySelector("button.call-app-btn").remove();
            document.querySelector(".collapse-tips .close-collapse-btn").click();
            document.querySelector(".guidance-wrap-item:last-child .wrap-item-btn").click();
            document.querySelector(".open-app-modal .cancel").click();
            document.querySelector(".btn-content button.cancel").click();
        """

        val urlScriptMap = mapOf(
                "facebook.com" to facebookHideSponsoredPostsJs,
                "zhihu.com" to zhihuDisablePopupJs,
                "www.jianshu.com" to jianshuJs,
        )
    }
}