// 검색창 script
document.addEventListener("DOMContentLoaded", function () {
  const input = document.querySelector(".search-input");
  const clearBtn = document.querySelector(".btn-clear");
  if (!input || !clearBtn) return;

  function toggleClearBtn() {
    clearBtn.style.opacity = input.value ? "0.5" : "0";
    clearBtn.style.pointerEvents = input.value ? "auto" : "none";
  }

  input.addEventListener("input", toggleClearBtn);
  clearBtn.addEventListener("click", () => {
    input.value = "";
    input.focus();
    toggleClearBtn();
  });

  toggleClearBtn(); // 초기 상태 동기화
});
