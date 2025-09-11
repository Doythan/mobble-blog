package org.example.mobble._util.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;

public class HtmlUtil {

    public class HtmlSanitizer {
        /**
         * 규칙:
         * 1) H1~H6 허용
         * 2) 목록: UL, LI 허용
         * 3) 이미지 허용
         * 4) 스크립트/하이퍼링크 등 외부 자원 불허
         * + 문단 유지용 P/BR 허용
         */
        private static final Safelist SAFE = new Safelist()
                // 허용 태그
                .addTags("h1", "h2", "h3", "h4", "h5", "h6", "ul", "li", "p", "br", "img")
                // 허용 속성 (이미지에 한정)
                .addAttributes("img", "src", "width", "height", "alt")
                // 프로토콜 제한: data 만 명시 (상대경로는 프로토콜이 없으므로 통과)
                .addProtocols("img", "src", "data");

        /**
         * HTML을 Jsoup로 살균하고, 추가적으로 외부 리소스 가능성 정리:
         * - 절대 http/https 이미지는 제거(또는 상대경로만 허용)
         * - a/script/style 등은 Safelist에 없으므로 자동 제거됨
         */
        public static String sanitize(String html) {
            if (html == null || html.isBlank()) return "";

            Document dirty = Jsoup.parseBodyFragment(html);
            Cleaner cleaner = new Cleaner(SAFE);
            Document clean = cleaner.clean(dirty);

            // 절대 URL 이미지는 제거 (외부 자원 금지 규칙 강화)
            stripExternalImageSrc(clean);

            // 출력 옵션
            clean.outputSettings()
                    .prettyPrint(false)
                    .escapeMode(Entities.EscapeMode.xhtml)
                    .charset("UTF-8");

            return clean.body().html();
        }

        /**
         * img[src^=http], img[src^=https] 제거하거나 상대경로/데이터URL만 남김
         * 필요 시 "제거" 대신 "src 비우기"로 바꿀 수 있음.
         */
        private static void stripExternalImageSrc(Document doc) {
            Elements bad = doc.select("img[src^=http], img[src^=https]");
            for (Element img : bad) {
                // 정책1) 완전 제거
                img.remove();

                // 정책2) 차선책: src 제거만 하고 alt 남기기
                // img.removeAttr("src");
            }
        }
    }
}
