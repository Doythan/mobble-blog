// 사이드바 애니메이션
(function () {
  const btn = document.getElementById("navToggle"); // 햄버거 버튼
  const nav = document.getElementById("sidenav"); // <aside id="sidenav">
  const overlay = document.getElementById("sidenavOverlay"); // <div id="sidenavOverlay">
  if (!btn || !nav || !overlay) return;

  // CSS와 맞추세요: .sidenav { transition: transform 260ms ... }
  const TRANSFORM_END = 220; // ms (여유 포함)
  let isOpen = false;
  let animating = false;
  let lastFocused = null;
  let timer = null;

  const clearTimer = () => {
    if (timer) {
      clearTimeout(timer);
      timer = null;
    }
  };

  // transitionend가 안 오는 경우를 대비한 안전 처리
  const onEndOnce = (el, cb) => {
    const done = (e) => {
      // transform 전환만 감지 (left로 애니메이션 하신다면 'left'로 바꾸세요)
      if (e && e.propertyName && e.propertyName !== "transform") return;
      el.removeEventListener("transitionend", done);
      clearTimer();
      cb();
    };
    el.addEventListener("transitionend", done);
    // fallback 타이머
    clearTimer();
    timer = setTimeout(() => {
      el.removeEventListener("transitionend", done);
      cb();
    }, TRANSFORM_END + 80);
  };

  const forceReflow = (el) => {
    void el.offsetWidth;
  };

  function openNav() {
    if (isOpen || animating) return;
    animating = true;
    lastFocused = document.activeElement;

    // 초기 상태로 리셋
    nav.classList.remove("closing", "open");
    if (nav.hasAttribute("hidden")) nav.removeAttribute("hidden");
    overlay.removeAttribute("hidden");

    // 초기 translateX(-100%) / opacity:0 상태를 브라우저에 확실히 적용
    forceReflow(nav);

    // 다음 프레임에 열기 → 트랜지션 발동
    requestAnimationFrame(() => {
      document.body.classList.add("body-lock"); // (옵션) 바디 스크롤 잠금
      overlay.classList.add("show");

      nav.classList.add("open");
      nav.setAttribute("aria-hidden", "false");
      btn.setAttribute("aria-expanded", "true");
      btn.classList.add("is-open"); // 햄버거→X 애니메이션 쓸 때

      onEndOnce(nav, () => {
        animating = false;
        isOpen = true;
        // 포커스 이동
        const first = nav.querySelector(
          'a,button,[tabindex]:not([tabindex="-1"])'
        );
        if (first) first.focus();
        document.addEventListener("keydown", onEsc);
      });
    });
  }

  function closeNav() {
    if (!isOpen || animating) return;
    animating = true;

    nav.classList.remove("open");
    nav.classList.add("closing");
    overlay.classList.remove("show");
    document.body.classList.remove("body-lock");

    nav.setAttribute("aria-hidden", "true");
    btn.setAttribute("aria-expanded", "false");
    btn.classList.remove("is-open");
    document.removeEventListener("keydown", onEsc);

    onEndOnce(nav, () => {
      nav.classList.remove("closing");
      isOpen = false;
      animating = false;
      overlay.setAttribute("hidden", ""); // 완전히 닫힌 뒤 숨김
      if (lastFocused) lastFocused.focus();
    });
  }

  function onEsc(e) {
    if (e.key === "Escape") closeNav();
  }

  btn.addEventListener("click", () => (isOpen ? closeNav() : openNav()));
  overlay.addEventListener("click", closeNav);
})();
