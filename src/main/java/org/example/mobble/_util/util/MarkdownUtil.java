package org.example.mobble._util.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class MarkdownUtil {
    public static String applyBasicMarkdown(String sanitizedHtml) {
        if (sanitizedHtml == null || sanitizedHtml.isBlank()) return sanitizedHtml;

        Document doc = Jsoup.parseBodyFragment(sanitizedHtml);
        Element body = doc.body();

        Elements children = body.children();

        Element container = new Element("div");
        boolean inList = false;
        Element currentUl = null;

        for (int i = 0; i < children.size(); i++) {
            Node node = children.get(i);

            // ✅ 람다(forEach) 대신 일반 for문 사용: inList/currentUl 갱신 가능
            java.util.List<Element> blocks = normalizeBlocks(node);
            for (int bi = 0; bi < blocks.size(); bi++) {
                Element block = blocks.get(bi);

                // 이미 의미 블록이면 그대로 복사
                if (isNonParagraphBlock(block)) {
                    if (inList) {
                        container.appendChild(currentUl);
                        inList = false;
                        currentUl = null;
                    }
                    container.appendChild(block.clone());
                    continue;
                }

                // <p>로 정규화
                if (!block.normalName().equals("p")) {
                    block = wrapToParagraph(block);
                }

                String raw = block.text();
                String trimmed = raw == null ? "" : raw.strip();

                // 빈 문단: 리스트 닫고 그대로 복사
                if (trimmed.isEmpty()) {
                    if (inList) {
                        container.appendChild(currentUl);
                        inList = false;
                        currentUl = null;
                    }
                    container.appendChild(block.clone());
                    continue;
                }

                // 헤딩 처리
                if (trimmed.startsWith("# ")) {
                    if (inList) {
                        container.appendChild(currentUl);
                        inList = false;
                        currentUl = null;
                    }
                    container.appendChild(makeHeading(block, 1));
                    continue;
                } else if (trimmed.startsWith("## ")) {
                    if (inList) {
                        container.appendChild(currentUl);
                        inList = false;
                        currentUl = null;
                    }
                    container.appendChild(makeHeading(block, 2));
                    continue;
                } else if (trimmed.startsWith("### ")) {
                    if (inList) {
                        container.appendChild(currentUl);
                        inList = false;
                        currentUl = null;
                    }
                    container.appendChild(makeHeading(block, 3));
                    continue;
                }

                // 불릿 처리
                if (isBullet(trimmed)) {
                    if (!inList) {
                        inList = true;
                        currentUl = new Element("ul");
                    }
                    currentUl.appendChild(makeListItem(block));
                    continue;
                }

                // 일반 문단
                if (inList) {
                    container.appendChild(currentUl);
                    inList = false;
                    currentUl = null;
                }
                container.appendChild(block.clone());
            }
        }

        if (inList && currentUl != null) {
            container.appendChild(currentUl);
        }

        body.html(container.html());
        return body.html();
    }


    // ----- helpers -----

    private static boolean isNonParagraphBlock(Element el) {
        String n = el.normalName();
        // 이미 의미 태그면 건드리지 않음
        return n.equals("h1") || n.equals("h2") || n.equals("h3") ||
                n.equals("ul") || n.equals("ol") ||
                n.equals("pre") || n.equals("blockquote") ||
                n.equals("table") || n.equals("hr");
    }

    private static boolean isBullet(String trimmed) {
        // "* " 또는 "- " + 공백 한 칸 뒤에 내용
        return trimmed.startsWith("* ") || trimmed.startsWith("- ");
    }

    private static Element makeHeading(Element fromP, int level) {
        String text = fromP.text().strip();
        String payload = switch (level) {
            case 1 -> text.substring(2).strip();   // "# "
            case 2 -> text.substring(3).strip();   // "## "
            default -> text.substring(4).strip();  // "### "
        };
        Element h = new Element("h" + level);
        // 가능하면 원래 p 안의 인라인(굵게/링크 등)을 유지하도록, 시작 토큰만 제거해서 innerHTML 재구성
        h.html(stripLeadingTokenPreserveInline(fromP, level));
        if (h.text().isBlank()) h.text(payload);
        return h;
    }

    private static Element makeListItem(Element fromP) {
        String trimmed = fromP.text().strip();
        String payload = trimmed.substring(2).strip(); // "* " or "- "
        Element li = new Element("li");
        // 인라인 유지 버전
        li.html(stripLeadingBulletPreserveInline(fromP));
        if (li.text().isBlank()) li.text(payload);
        return li;
    }

    private static String stripLeadingTokenPreserveInline(Element p, int level) {
        // p의 HTML을 가져와서 선두의 토큰("# ", "## ", "### ")만 제거
        String html = p.html(); // 인라인 태그 포함
        // 줄 시작에서만 매칭되도록
        String pattern = switch (level) {
            case 1 -> "^(\\s*#\\s+)";
            case 2 -> "^(\\s*##\\s+)";
            default -> "^(\\s*###\\s+)";
        };
        return html.replaceFirst(pattern, "");
    }

    private static String stripLeadingBulletPreserveInline(Element p) {
        String html = p.html();
        return html.replaceFirst("^(\\s*[\\*\\-]\\s+)", "");
    }

    private static Element wrapToParagraph(Node node) {
        if (node instanceof TextNode tn) {
            Element p = new Element("p");
            p.text(tn.text());
            return p;
        } else if (node instanceof Element e) {
            if (e.normalName().equals("p")) return e;
            Element p = new Element("p");
            p.html(e.outerHtml());
            return p;
        } else {
            Element p = new Element("p");
            p.text(node.outerHtml());
            return p;
        }
    }

    /**
     * 다양한 블록 요소 내부에 들어있는 <p>를 낱낱이 꺼내 블록 단위로 평탄화
     */
    private static java.util.List<Element> normalizeBlocks(Node node) {
        java.util.List<Element> out = new java.util.ArrayList<>();
        if (node instanceof Element el) {
            // 블록성이 강한 요소는 자식 <p>를 그대로 순회
            if (el.childrenSize() == 0) {
                out.add(el);
            } else {
                for (Element c : el.children()) {
                    // <p>면 그대로, 그 외는 재귀적으로 평탄화
                    if (c.normalName().equals("p") || isNonParagraphBlock(c)) {
                        out.add(c);
                    } else {
                        out.addAll(normalizeBlocks(c));
                    }
                }
            }
        } else if (node instanceof TextNode) {
            out.add(wrapToParagraph(node));
        }
        return out;
    }
}
