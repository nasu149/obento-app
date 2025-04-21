import './App.css'
import Home from './Home'
import { CalendarComponent } from './CalendarComponent'
// import { BentoViewer } from './components/BentoViewer'

function App() {
  // const today: Date = new Date();
  // const todayString: string = today.toLocaleDateString('sv-SE');
  const userId = "eee";

  return (
    <>
      {/* <BentoViewer userId={userId} date={todayString} /> */}
      <Home userId={userId} />
      <CalendarComponent userId={userId} />
    </>
  )
}

export default App
