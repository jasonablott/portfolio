import { useState } from 'react';

// component to make widgets for login page input fields
export function InputWidget({name, placeholder, setData, username, roomname}) {

  // state to track a string this widget uses
  const [value, setValue] = useState("");

  // method to update value on user input to textarea
  const updateData = (event) => {
    setValue(event.target.value);
    setData(event.target.value);
  }

  // code to return component 
  return (
      <div id="inputDiv">
          <div>
            {name}
          <div>
          <br/>
          <textarea id ="input" 
            value={value}
            onChange={updateData}
            placeholder={placeholder}>
            </textarea>
          </div>
            <br/>
          </div>
      </div>
  )
}