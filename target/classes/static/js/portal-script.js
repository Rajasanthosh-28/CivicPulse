/**
 * CivicPulse Citizen Portal - Core Interactive JavaScript
 * Handles dark/light theme persistence, sidebar toggling, notification polling,
 * live clock, animated counters, table search, multi-step wizards, star ratings, and dropdowns.
 */

document.addEventListener('DOMContentLoaded', () => {
    initTheme();
    initSidebarToggle();
    initUserDropdown();
    initLiveClock();
    initCounters();
    initNotificationPolling();
    initTableSearch();
    initMultiStepWizard();
    initStarRating();
});

/* ==========================================================================
   1. Dark Mode & Theme Management
   ========================================================================== */
function initTheme() {
    const darkModeToggle = document.getElementById('darkModeToggle');
    const savedTheme = localStorage.getItem('civicpulse-theme');
    
    // Apply saved theme or default to light
    if (savedTheme === 'dark') {
        document.documentElement.setAttribute('data-theme', 'dark');
    } else {
        document.documentElement.removeAttribute('data-theme');
    }

    if (darkModeToggle) {
        darkModeToggle.addEventListener('click', () => {
            const currentTheme = document.documentElement.getAttribute('data-theme');
            if (currentTheme === 'dark') {
                document.documentElement.removeAttribute('data-theme');
                localStorage.setItem('civicpulse-theme', 'light');
            } else {
                document.documentElement.setAttribute('data-theme', 'dark');
                localStorage.setItem('civicpulse-theme', 'dark');
            }
            window.dispatchEvent(new Event('themeChanged'));
        });
    }
}

/* ==========================================================================
   2. Sidebar Toggle (#hamburgerBtn)
   ========================================================================== */
function initSidebarToggle() {
    const hamburgerBtn = document.getElementById('hamburgerBtn');
    const sidebar = document.getElementById('sidebar');

    if (hamburgerBtn && sidebar) {
        hamburgerBtn.addEventListener('click', () => {
            sidebar.classList.toggle('collapsed');
        });
    }
}

/* ==========================================================================
   3. User Dropdown (#userDropdownToggle toggling .user-dropdown-menu.show)
   ========================================================================== */
function initUserDropdown() {
    const dropdownToggle = document.getElementById('userDropdownToggle');
    const dropdownMenu = document.getElementById('userDropdownMenu');

    if (dropdownToggle && dropdownMenu) {
        dropdownToggle.addEventListener('click', (e) => {
            e.stopPropagation();
            dropdownMenu.classList.toggle('show');
        });

        document.addEventListener('click', (e) => {
            if (!dropdownToggle.contains(e.target) && !dropdownMenu.contains(e.target)) {
                dropdownMenu.classList.remove('show');
            }
        });
    }
}

/* ==========================================================================
   4. Live Clock (#liveClock updating every second)
   ========================================================================== */
function initLiveClock() {
    const liveClock = document.getElementById('liveClock');
    if (!liveClock) return;

    const updateClock = () => {
        const now = new Date();
        const options = { 
            weekday: 'short', 
            year: 'numeric', 
            month: 'short', 
            day: 'numeric', 
            hour: '2-digit', 
            minute: '2-digit', 
            second: '2-digit' 
        };
        liveClock.textContent = now.toLocaleDateString('en-US', options);
    };

    updateClock();
    setInterval(updateClock, 1000);
}

/* ==========================================================================
   5. Animated Number Counters (.counter counting up to data-target)
   ========================================================================== */
function initCounters() {
    const counters = document.querySelectorAll('.counter');
    if (counters.length === 0) return;

    const animateCounter = (counter) => {
        const target = parseFloat(counter.getAttribute('data-target')) || 0;
        const duration = 1200; // ms
        const frameRate = 16; // ms per frame (~60fps)
        const totalFrames = duration / frameRate;
        const increment = target / totalFrames;
        let current = 0;

        const timer = setInterval(() => {
            current += increment;
            if (current >= target) {
                counter.textContent = Number.isInteger(target) ? target.toString() : target.toFixed(1);
                clearInterval(timer);
            } else {
                counter.textContent = Number.isInteger(target) ? Math.floor(current).toString() : current.toFixed(1);
            }
        }, frameRate);
    };

    const observer = new IntersectionObserver((entries, obs) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                animateCounter(entry.target);
                obs.unobserve(entry.target);
            }
        });
    }, { threshold: 0.2 });

    counters.forEach(counter => observer.observe(counter));
}

/* ==========================================================================
   6. Notification Badge (#notifBadge polling /api/notifications/count)
   ========================================================================== */
function initNotificationPolling() {
    const notifBadge = document.getElementById('notifBadge');
    if (!notifBadge) return;

    const fetchNotificationCount = async () => {
        try {
            const response = await fetch('/api/notifications/count');
            if (response.ok) {
                const data = await response.json();
                const count = data.count || 0;
                notifBadge.textContent = count;
                if (count > 0) {
                    notifBadge.style.display = 'flex';
                } else {
                    notifBadge.style.display = 'none';
                }
            }
        } catch (error) {
            console.error('Error fetching notification count:', error);
        }
    };

    fetchNotificationCount();
    // Poll every 30 seconds
    setInterval(fetchNotificationCount, 30000);
}

/* ==========================================================================
   7. Table Search Filter (#tableSearchInput filtering rows)
   ========================================================================== */
function initTableSearch() {
    const searchInput = document.getElementById('tableSearchInput') || document.getElementById('navbarSearch');
    const dataTable = document.querySelector('.data-table');
    if (!searchInput || !dataTable) return;

    searchInput.addEventListener('input', (e) => {
        const query = e.target.value.toLowerCase().trim();
        const tbody = dataTable.querySelector('tbody');
        if (!tbody) return;

        const rows = tbody.querySelectorAll('tr');
        rows.forEach(row => {
            const textContent = row.textContent.toLowerCase();
            if (textContent.includes(query)) {
                row.style.display = '';
            } else {
                row.style.display = 'none';
            }
        });
    });
}

/* ==========================================================================
   8. Multi-step Wizard Navigation (#nextBtn, #prevBtn, .step-content, .step-item)
   ========================================================================== */
function initMultiStepWizard() {
    const nextBtn = document.getElementById('nextBtn');
    const prevBtn = document.getElementById('prevBtn');
    const stepContents = document.querySelectorAll('.step-content');
    const stepItems = document.querySelectorAll('.step-item');

    if (stepContents.length === 0 || !nextBtn) return;

    let currentStep = 0;

    const updateWizardState = () => {
        stepContents.forEach((content, index) => {
            if (index === currentStep) {
                content.classList.add('active');
            } else {
                content.classList.remove('active');
            }
        });

        stepItems.forEach((item, index) => {
            item.classList.remove('active', 'completed');
            if (index < currentStep) {
                item.classList.add('completed');
            } else if (index === currentStep) {
                item.classList.add('active');
            }
        });

        if (prevBtn) {
            prevBtn.style.display = currentStep === 0 ? 'none' : 'inline-flex';
        }

        if (currentStep === stepContents.length - 1) {
            nextBtn.textContent = 'Submit Complaint';
            nextBtn.classList.remove('btn-secondary');
            nextBtn.classList.add('btn-success');
        } else {
            nextBtn.textContent = 'Next Step';
            nextBtn.classList.remove('btn-success');
            nextBtn.classList.add('btn-primary');
        }
    };

    nextBtn.addEventListener('click', (e) => {
        if (currentStep < stepContents.length - 1) {
            e.preventDefault();
            currentStep++;
            updateWizardState();
        }
    });

    if (prevBtn) {
        prevBtn.addEventListener('click', (e) => {
            e.preventDefault();
            if (currentStep > 0) {
                currentStep--;
                updateWizardState();
            }
        });
    }

    updateWizardState();
}

/* ==========================================================================
   9. Star Rating Selection (.star setting hidden input #ratingInput)
   ========================================================================== */
function initStarRating() {
    const stars = document.querySelectorAll('.star-rating .star');
    const ratingInput = document.getElementById('ratingInput');

    if (stars.length === 0) return;

    stars.forEach(star => {
        star.addEventListener('click', () => {
            const value = star.getAttribute('data-value');
            if (ratingInput) {
                ratingInput.value = value;
            }

            stars.forEach(s => {
                const sValue = s.getAttribute('data-value');
                if (parseInt(sValue) <= parseInt(value)) {
                    s.classList.add('active');
                } else {
                    s.classList.remove('active');
                }
            });
        });
    });
}
