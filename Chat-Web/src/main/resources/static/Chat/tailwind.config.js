/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      keyframes:{
        border_breathe:{
          '0%':{'box-shadow': '0 0 4px gold'},
          '100%':{'box-shadow':'0 0 4px rgba(255,215,0,0.8)'},
        },
      },
      animation:{
        breathe: 'border_breathe 5s infinite alternate',
      },
    }
  },
  plugins: [
     require('@tailwindcss/forms'),
  require('@tailwindcss/typography'),
  ],
}
