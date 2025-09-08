// 글 상세보기 : 햄버거 메뉴
(function () {
  const trigger = document.querySelector(".post-menu-trigger");
  const menu = document.getElementById("postMenu");
  if (!trigger || !menu) return;

  function openMenu() {
    trigger.setAttribute("aria-expanded", "true");
    menu.hidden = false;
    document.addEventListener("click", onDocClick);
    document.addEventListener("keydown", onKey);
  }
  function closeMenu() {
    trigger.setAttribute("aria-expanded", "false");
    menu.hidden = true;
    document.removeEventListener("click", onDocClick);
    document.removeEventListener("keydown", onKey);
  }
  function onDocClick(e) {
    if (menu.contains(e.target) || trigger.contains(e.target)) return;
    closeMenu();
  }
  function onKey(e) {
    if (e.key === "Escape") closeMenu();
  }
  trigger.addEventListener("click", () => {
    const expanded = trigger.getAttribute("aria-expanded") === "true";
    expanded ? closeMenu() : openMenu();
  });
})();
