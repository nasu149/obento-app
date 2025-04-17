import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import Home from './Home'
import Calendar from './Calendar'

function App() {
  const [count, setCount] = useState(0)

  return (
    <>
      <Home />
      <Calendar />
    </>
  )
}

export default App
