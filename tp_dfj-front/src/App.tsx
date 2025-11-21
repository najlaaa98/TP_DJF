import {BrowserRouter,Routes, Route} from 'react-router-dom';

import './App.css'
import ListeUser from './composant/ListeUser'
import CreateUser from './composant/CreateUser';
import ModifierUser from './composant/ModifierUser';

function App() {

  return (
    <BrowserRouter>
      <Routes>

        <Route path="/" element={<ListeUser />} />
        <Route path="/create" element={<CreateUser />} />
        <Route path="/edit/:id" element={<ModifierUser />} />

      </Routes>
    </BrowserRouter>
  )
}

export default App
