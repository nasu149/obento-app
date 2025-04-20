import './App.css'
import Home from './Home'
import Calendar from './Calendar'
// import { BentoViewer } from './components/BentoViewer'

function App() {
  // const today: Date = new Date();
  // const todayString: string = today.toLocaleDateString('sv-SE');
  const userId = "string18";

  return (
    <>
      {/* <BentoViewer userId={userId} date={todayString} /> */}
      <Home userId={userId} />
      <Calendar />
    </>
  )
}

export default App
