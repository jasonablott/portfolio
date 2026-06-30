/* ─── Navigation ─────────────────────────────────────────────── */

const nav       = document.getElementById('nav');
const navToggle = document.getElementById('navToggle');
const navLinks  = document.querySelector('.nav-links');

// Shrink nav on scroll
window.addEventListener('scroll', () => {
  nav.classList.toggle('scrolled', window.scrollY > 24);
}, { passive: true });

// Mobile menu toggle
navToggle.addEventListener('click', () => {
  const open = navToggle.classList.toggle('open');
  navLinks.classList.toggle('open', open);
  navToggle.setAttribute('aria-expanded', open);
});

// Close menu on nav link click
navLinks.querySelectorAll('a').forEach(link => {
  link.addEventListener('click', () => {
    navToggle.classList.remove('open');
    navLinks.classList.remove('open');
  });
});


/* ─── Scroll Reveal ──────────────────────────────────────────── */

const revealObserver = new IntersectionObserver(
  (entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('visible');
        revealObserver.unobserve(entry.target);
      }
    });
  },
  { threshold: 0.12, rootMargin: '0px 0px -40px 0px' }
);

// Attach reveal class to section children
document.querySelectorAll(
  '.section-label, .section-title, .section-subtitle, ' +
  '.about-text, .about-stats, .stat-card, ' +
  '.skill-group, .project-card, .project-placeholder, ' +
  '.social-links, .btn-large'
).forEach((el, i) => {
  el.classList.add('reveal');
  el.style.transitionDelay = `${(i % 4) * 0.07}s`;
  revealObserver.observe(el);
});


/* ─── Footer Year ────────────────────────────────────────────── */

const yearEl = document.getElementById('footerYear');
if (yearEl) yearEl.textContent = `© ${new Date().getFullYear()}`;


/* ─── Active Nav Highlight ───────────────────────────────────── */

const sections = document.querySelectorAll('section[id]');

const sectionObserver = new IntersectionObserver(
  (entries) => {
    entries.forEach(entry => {
      if (!entry.isIntersecting) return;
      const id = entry.target.getAttribute('id');
      navLinks.querySelectorAll('a').forEach(a => {
        a.style.color = a.getAttribute('href') === `#${id}`
          ? 'var(--text)'
          : '';
      });
    });
  },
  { threshold: 0.4 }
);

sections.forEach(s => sectionObserver.observe(s));


/* ─── Project Cards Helper ───────────────────────────────────── */
// Use this function to programmatically add project cards later.
//
// addProject({
//   title:       'Project Name',
//   description: 'What it does.',
//   tags:        ['React', 'Node.js'],
//   links:       { github: '#', live: '#' }
// });

function addProject({ title, description, tags = [], links = {} }) {
  const grid  = document.getElementById('projectsGrid');
  const placeholder = grid.querySelector('.project-placeholder');
  if (placeholder) placeholder.remove();

  const card  = document.createElement('div');
  card.className = 'project-card reveal';

  const tagsHtml  = tags.map(t => `<span class="project-card-tag">${t}</span>`).join('');
  const linksHtml = Object.entries(links)
    .map(([label, href]) => `<a href="${href}" target="_blank" rel="noopener">${label} ↗</a>`)
    .join('');

  card.innerHTML = `
    <div class="project-card-title">${title}</div>
    <div class="project-card-desc">${description}</div>
    <div class="project-card-tags">${tagsHtml}</div>
    <div class="project-card-links">${linksHtml}</div>
  `;

  grid.appendChild(card);
  revealObserver.observe(card);
}
