const sharp = require('sharp');
const fs = require('fs');
const path = require('path');

const IMG_DIR = 'd:/javacode/vsCode/html/sky/img';
const ICONS_DIR = path.join(IMG_DIR, 'icons');

const RED = '#DA291C', GOLD = '#FFC72C', DARK = '#1a1a1a', WHITE = '#FFFFFF';

async function svgToPng(svg, w, h) {
  return sharp(Buffer.from(svg)).resize(w, h).png().toBuffer();
}
function save(p, buf) { fs.writeFileSync(p, buf); console.log('Saved:', path.basename(p)); }

// ===== 1. LOGIN TEXT LOGO (icon_logo) - displayed at 149x38, create 2x = 298x76 =====
async function createTextLogo() {
  const w = 298, h = 76;
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="${w}" height="${h}" viewBox="0 0 ${w} ${h}">
    <defs>
      <linearGradient id="g" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" style="stop-color:#FFE57F"/>
        <stop offset="40%" style="stop-color:#FFC72C"/>
        <stop offset="100%" style="stop-color:#FF8F00"/>
      </linearGradient>
    </defs>
    <text x="2" y="56" font-size="46" font-weight="900" fill="url(#g)" font-family="Arial Black,Arial,sans-serif">M</text>
    <line x1="44" y1="18" x2="44" y2="58" stroke="rgba(255,199,44,0.5)" stroke-width="1.5"/>
    <text x="56" y="52" font-size="26" font-weight="700" fill="#FFFFFF" font-family="Microsoft YaHei,PingFang SC,sans-serif" letter-spacing="2">饿了吧外卖</text>
  </svg>`;
  return svgToPng(svg, w, h);
}

// ===== 2. SIDEBAR LOGO (logo) - displayed in sidebar, ~180x44 =====
async function createSidebarLogo() {
  const w = 360, h = 88;
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="${w}" height="${h}" viewBox="0 0 ${w} ${h}">
    <defs>
      <linearGradient id="g" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" style="stop-color:#FFE57F"/>
        <stop offset="40%" style="stop-color:#FFC72C"/>
        <stop offset="100%" style="stop-color:#FF8F00"/>
      </linearGradient>
    </defs>
    <text x="4" y="64" font-size="52" font-weight="900" fill="url(#g)" font-family="Arial Black,Arial,sans-serif">M</text>
    <line x1="50" y1="22" x2="50" y2="66" stroke="rgba(255,199,44,0.5)" stroke-width="1.5"/>
    <text x="64" y="60" font-size="28" font-weight="700" fill="#FFFFFF" font-family="Microsoft YaHei,PingFang SC,sans-serif" letter-spacing="2">饿了吧外卖</text>
  </svg>`;
  return svgToPng(svg, w, h);
}

// ===== 3. MINI LOGO - pure M for collapsed sidebar =====
async function createMiniLogo() {
  const w = 120, h = 120;
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="${w}" height="${h}" viewBox="0 0 ${w} ${h}">
    <defs>
      <linearGradient id="g" x1="0%" y1="0%" x2="0%" y2="100%">
        <stop offset="0%" style="stop-color:#FFE57F"/>
        <stop offset="50%" style="stop-color:#FFC72C"/>
        <stop offset="100%" style="stop-color:#FF8F00"/>
      </linearGradient>
    </defs>
    <rect width="${w}" height="${h}" rx="20" fill="#DA291C"/>
    <text x="${w*0.5}" y="${w*0.72}" font-size="80" font-weight="900" fill="url(#g)" text-anchor="middle" font-family="Arial Black,Arial,sans-serif">M</text>
  </svg>`;
  return svgToPng(svg, w, h);
}

// ===== 4. LOGIN ILLUSTRATION - professional food delivery =====
async function createLoginIllustration() {
  const w = 960, h = 600;
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="${w}" height="${h}" viewBox="0 0 ${w} ${h}">
    <defs>
      <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" style="stop-color:#1a1a1a"/>
        <stop offset="40%" style="stop-color:#2a0a0a"/>
        <stop offset="100%" style="stop-color:#DA291C"/>
      </linearGradient>
      <radialGradient id="glow" cx="45%" cy="40%" r="50%">
        <stop offset="0%" style="stop-color:rgba(218,41,28,.5)"/>
        <stop offset="100%" style="stop-color:transparent"/>
      </radialGradient>
    </defs>
    <rect width="${w}" height="${h}" fill="url(#bg)"/>
    <ellipse cx="${w*0.45}" cy="${h*0.4}" rx="${w*0.55}" ry="${h*0.5}" fill="url(#glow)"/>
    <!-- Large decorative M -->
    <text x="${w*0.48}" y="${h*0.58}" font-size="380" font-weight="900" fill="rgba(255,199,44,.04)" text-anchor="middle" font-family="Arial Black">M</text>
    <!-- Delivery scooter -->
    <g transform="translate(${w*0.52}, ${h*0.62})">
      <ellipse cx="0" cy="50" rx="70" ry="10" fill="rgba(0,0,0,.35)"/>
      <rect x="-40" y="8" width="80" height="24" rx="8" fill="#222"/>
      <rect x="-16" y="-14" width="32" height="24" rx="5" fill="#333"/>
      <line x1="10" y1="-14" x2="22" y2="-52" stroke="#444" stroke-width="4" stroke-linecap="round"/>
      <rect x="14" y="-58" width="18" height="8" rx="3" fill="#555"/>
      <circle cx="-28" cy="34" r="18" fill="#111"/><circle cx="-28" cy="34" r="8" fill="#555"/>
      <circle cx="28" cy="34" r="18" fill="#111"/><circle cx="28" cy="34" r="8" fill="#555"/>
      <rect x="-50" y="-34" width="44" height="38" rx="5" fill="#FFC72C"/>
      <text x="-28" y="-10" font-size="11" font-weight="bold" fill="#5D4037" text-anchor="middle" font-family="Microsoft YaHei,Arial">外卖</text>
    </g>
    <!-- Food circles -->
    <circle cx="${w*0.2}" cy="${h*0.3}" r="42" fill="none" stroke="rgba(255,199,44,.15)" stroke-width="1.5"/>
    <text x="${w*0.2}" y="${h*0.32}" font-size="40" text-anchor="middle">🍔</text>
    <circle cx="${w*0.78}" cy="${h*0.28}" r="36" fill="none" stroke="rgba(255,199,44,.12)" stroke-width="1.5"/>
    <text x="${w*0.78}" y="${h*0.3}" font-size="32" text-anchor="middle">🍟</text>
    <circle cx="${w*0.2}" cy="${h*0.68}" r="28" fill="none" stroke="rgba(255,199,44,.1)" stroke-width="1"/>
    <text x="${w*0.2}" y="${h*0.7}" font-size="24" text-anchor="middle">🥤</text>
    <!-- Speed lines -->
    <g stroke="rgba(255,255,255,.08)" stroke-width="2" stroke-linecap="round">
      <line x1="${w*0.64}" y1="${h*0.5}" x2="${w*0.84}" y2="${h*0.5}"/>
      <line x1="${w*0.66}" y1="${h*0.55}" x2="${w*0.88}" y2="${h*0.55}"/>
      <line x1="${w*0.68}" y1="${h*0.6}" x2="${w*0.82}" y2="${h*0.6}"/>
    </g>
    <text x="${w*0.5}" y="${h*0.22}" font-size="16" fill="rgba(255,255,255,.35)" text-anchor="middle" font-family="Arial" letter-spacing="8">PREMIUM DELIVERY</text>
    <text x="${w*0.5}" y="${h*0.9}" font-size="13" fill="rgba(255,255,255,.25)" text-anchor="middle" font-family="Microsoft YaHei,Arial" letter-spacing="4">美味 · 即刻 · 送达</text>
    <line x1="${w*0.32}" y1="${h*0.93}" x2="${w*0.68}" y2="${h*0.93}" stroke="rgba(255,199,44,.2)" stroke-width="1"/>
  </svg>`;
  return svgToPng(svg, w, h);
}

// ===== 5. PWA ICONS =====
async function createPwaIcon(size) {
  const svg = `<svg xmlns="http://www.w3.org/2000/svg" width="${size}" height="${size}" viewBox="0 0 ${size} ${size}">
    <defs>
      <linearGradient id="gg" x1="0%" y1="0%" x2="0%" y2="100%">
        <stop offset="0%" style="stop-color:#FFE57F"/><stop offset="100%" style="stop-color:#FF8F00"/>
      </linearGradient>
    </defs>
    <rect width="${size}" height="${size}" rx="${size*0.22}" fill="#DA291C"/>
    <text x="${size*0.5}" y="${size*0.72}" font-size="${size*0.65}" font-weight="900" fill="url(#gg)" text-anchor="middle" font-family="Arial Black,Arial">M</text>
  </svg>`;
  return svgToPng(svg, size, size);
}

async function main() {
  console.log('Generating v3 images...\n');
  save(path.join(IMG_DIR, 'icon_logo.38b01728.png'), await createTextLogo());
  save(path.join(IMG_DIR, 'logo.38b01728.png'), await createSidebarLogo());
  save(path.join(IMG_DIR, 'mini-logo.bf141cfc.png'), await createMiniLogo());
  save(path.join(IMG_DIR, 'login-l.6ef9d260.png'), await createLoginIllustration());

  const icons = [
    ['android-chrome-192x192.png',192],['android-chrome-512x512.png',512],
    ['apple-touch-icon-120x120.png',120],['apple-touch-icon-152x152.png',152],
    ['apple-touch-icon-180x180.png',180],['apple-touch-icon-60x60.png',60],
    ['apple-touch-icon-76x76.png',76],['apple-touch-icon.png',180],
    ['favicon-16x16.png',16],['favicon-32x32.png',32],
    ['msapplication-icon-144x144.png',144],['mstile-150x150.png',150],
  ];
  for (const [f, s] of icons) save(path.join(ICONS_DIR, f), await createPwaIcon(s));

  const safariSvg = `<svg xmlns="http://www.w3.org/2000/svg" width="700" height="700" viewBox="0 0 700 700">
    <defs><linearGradient id="g2" x1="0%" y1="0%" x2="0%" y2="100%"><stop offset="0%" style="stop-color:#FFE57F"/><stop offset="100%" style="stop-color:#FFC72C"/></linearGradient></defs>
    <rect width="700" height="700" rx="150" fill="#DA291C"/>
    <text x="350" y="510" font-size="480" font-weight="900" fill="url(#g2)" text-anchor="middle" font-family="Arial Black,Arial">M</text>
  </svg>`;
  save(path.join(ICONS_DIR, 'safari-pinned-tab.svg'), Buffer.from(safariSvg));
  console.log('\nDone!');
}
main().catch(e => { console.error(e); process.exit(1); });
