/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: '#2563eb', // Highlighting Blue
        secondary: '#64748b', // Slate Grey
        background: '#f8fafc', // Off-white/Light Gray
      },
      boxShadow: {
        'soft': '0 10px 40px -10px rgba(0,0,0,0.05)',
        'glow': '0 0 20px rgba(37, 99, 235, 0.15)',
      }
    },
  },
  plugins: [],
}
