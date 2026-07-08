/* ============================================================
   CivicPulse — Login Page JavaScript
   ============================================================ */

document.addEventListener('DOMContentLoaded', () => {

    // ---- Generate Floating Particles ----
    createParticles();

    // ---- Role Tab Switching ----
    initRoleTabs();

    // ---- Password Show/Hide Toggles ----
    initPasswordToggles();

    // ---- Password Strength Indicators ----
    initPasswordStrength();

    // ---- Login Button Ripple & Loading ----
    initLoginButtons();

    // ---- Dark Mode Toggle ----
    initDarkMode();

    // ---- Auto-dismiss Alerts ----
    initAlerts();
});


/* ========== Floating Particles ========== */
function createParticles() {
    const container = document.querySelector('.bg-animated');
    if (!container) return;

    const count = window.innerWidth < 576 ? 12 : 25;

    for (let i = 0; i < count; i++) {
        const particle = document.createElement('div');
        particle.classList.add('particle');

        const size = Math.random() * 6 + 3;
        particle.style.width = `${size}px`;
        particle.style.height = `${size}px`;
        particle.style.left = `${Math.random() * 100}%`;
        particle.style.animationDuration = `${Math.random() * 15 + 10}s`;
        particle.style.animationDelay = `${Math.random() * 10}s`;

        container.appendChild(particle);
    }
}


/* ========== Role Tab Switching ========== */
function initRoleTabs() {
    const tabs = document.querySelectorAll('.role-tab');
    const forms = document.querySelectorAll('.role-form');

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const target = tab.getAttribute('data-role');

            // Update tabs
            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');

            // Update forms
            forms.forEach(f => {
                f.classList.remove('active');
                if (f.id === `form-${target}`) {
                    f.classList.add('active');
                }
            });
        });
    });
}


/* ========== Password Show/Hide ========== */
function initPasswordToggles() {
    document.querySelectorAll('.password-toggle').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const input = btn.closest('.input-wrapper').querySelector('.form-input');
            const icon = btn.querySelector('i');

            if (input.type === 'password') {
                input.type = 'text';
                icon.classList.remove('bi-eye');
                icon.classList.add('bi-eye-slash');
            } else {
                input.type = 'password';
                icon.classList.remove('bi-eye-slash');
                icon.classList.add('bi-eye');
            }
        });
    });
}


/* ========== Password Strength Indicator ========== */
function initPasswordStrength() {
    document.querySelectorAll('.password-input').forEach(input => {
        const wrapper = input.closest('.form-group');
        const strengthBar = wrapper ? wrapper.querySelector('.strength-fill') : null;
        const strengthText = wrapper ? wrapper.querySelector('.strength-text') : null;

        if (!strengthBar || !strengthText) return;

        input.addEventListener('input', () => {
            const val = input.value;
            const result = calculateStrength(val);

            strengthBar.style.width = result.percent + '%';
            strengthBar.style.background = result.color;
            strengthText.textContent = val.length > 0 ? result.label : '';
            strengthText.style.color = result.color;
        });
    });
}

function calculateStrength(password) {
    let score = 0;
    if (password.length === 0) return { percent: 0, color: 'transparent', label: '' };
    if (password.length >= 6) score++;
    if (password.length >= 10) score++;
    if (/[a-z]/.test(password) && /[A-Z]/.test(password)) score++;
    if (/\d/.test(password)) score++;
    if (/[^a-zA-Z0-9]/.test(password)) score++;

    const levels = [
        { percent: 15, color: '#EF4444', label: 'Very Weak' },
        { percent: 30, color: '#F59E0B', label: 'Weak' },
        { percent: 55, color: '#F59E0B', label: 'Fair' },
        { percent: 75, color: '#22C55E', label: 'Good' },
        { percent: 100, color: '#22C55E', label: 'Strong' }
    ];

    return levels[Math.min(score, levels.length - 1)];
}


/* ========== Login Button — Ripple & Loading ========== */
function initLoginButtons() {
    document.querySelectorAll('.btn-login').forEach(btn => {
        // Ripple on click
        btn.addEventListener('click', function (e) {
            // Create ripple
            const ripple = document.createElement('span');
            ripple.classList.add('ripple');

            const rect = this.getBoundingClientRect();
            const size = Math.max(rect.width, rect.height);
            ripple.style.width = ripple.style.height = `${size}px`;
            ripple.style.left = `${e.clientX - rect.left - size / 2}px`;
            ripple.style.top = `${e.clientY - rect.top - size / 2}px`;

            this.appendChild(ripple);
            ripple.addEventListener('animationend', () => ripple.remove());
        });

        // Loading state on form submit
        const form = btn.closest('form');
        if (form) {
            form.addEventListener('submit', function (e) {
                // Basic client-side check — allow form submit to proceed
                const inputs = form.querySelectorAll('input[required]');
                let valid = true;
                inputs.forEach(inp => {
                    if (!inp.value.trim()) valid = false;
                });

                if (valid) {
                    btn.classList.add('loading');
                }
            });
        }
    });
}


/* ========== Dark Mode ========== */
function initDarkMode() {
    const toggle = document.getElementById('darkModeToggle');
    if (!toggle) return;

    // Load saved preference
    const saved = localStorage.getItem('civicpulse-theme');
    if (saved === 'dark') {
        document.documentElement.setAttribute('data-theme', 'dark');
    }

    toggle.addEventListener('click', () => {
        const current = document.documentElement.getAttribute('data-theme');
        const next = current === 'dark' ? 'light' : 'dark';

        if (next === 'dark') {
            document.documentElement.setAttribute('data-theme', 'dark');
        } else {
            document.documentElement.removeAttribute('data-theme');
        }

        localStorage.setItem('civicpulse-theme', next);
    });
}


/* ========== Auto-dismiss Alerts ========== */
function initAlerts() {
    document.querySelectorAll('.alert-custom').forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
            alert.style.opacity = '0';
            alert.style.transform = 'translateY(-8px)';
            setTimeout(() => alert.remove(), 500);
        }, 6000);
    });
}
