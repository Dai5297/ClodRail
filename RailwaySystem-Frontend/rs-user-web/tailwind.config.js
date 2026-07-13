/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#1677FF',
          hover: '#4096FF',
          active: '#0958D9',
          soft: '#EAF3FF',
        },
        navy: '#0B2559',
        action: {
          DEFAULT: '#FF8200',
          hover: '#F07200',
          soft: '#FFF3E6',
        },
        ink: '#172033',
        background: '#F5F8FC',
        line: '#DDE6F0',
        secondary: '#5F6B7A',
      },
      fontFamily: {
        sans: [
          'PingFang SC',
          'Microsoft YaHei',
          'Noto Sans CJK SC',
          'Source Han Sans SC',
          'Helvetica Neue',
          'Arial',
          'sans-serif',
        ],
        data: [
          'DIN Alternate',
          'DIN Condensed',
          'Arial Narrow',
          'Roboto Condensed',
          'Arial',
          'sans-serif',
        ],
      },
      borderRadius: {
        control: '8px',
        card: '12px',
        hero: '16px',
      },
      boxShadow: {
        soft: '0 8px 28px rgba(11, 37, 89, 0.08)',
        card: '0 10px 32px rgba(11, 37, 89, 0.10)',
        focus: '0 0 0 3px rgba(22, 119, 255, 0.18)',
        glow: '0 0 20px rgba(22, 119, 255, 0.14)',
      }
    },
  },
  plugins: [],
}
