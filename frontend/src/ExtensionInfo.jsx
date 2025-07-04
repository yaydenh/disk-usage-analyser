import { useEffect, useRef, useState } from 'react';
import './ExtensionInfo.css'
import './FileTable.css'

import { VariableSizeGrid  as Grid } from 'react-window'
import AutoSizer from "react-virtualized-auto-sizer";
import LinearProgress from '@mui/material/LinearProgress';

function ExtensionInfo({ root, selectedExtension, setSelectedExtension, treeMapReady }) {

  const [ columnWidths, setColumnWidths ] = useState([ 100, 100, 80, 200 ]);
  const [ resizing, setResizing ] = useState({index: null, startX: 0, startWidth: 0});
  const columnMapping = ['extension', 'count', 'colour', 'description'];
  const headingRef = useRef();

  const [ data, setData ] = useState([]);
  const gridRef = useRef();

  const [ fileTypes, setFileTypes ] = useState({});

  // load extension-to-description json file
  useEffect(() => {
    (async () => {
      try {
        const res = await fetch('/extensions.final.json');
        const data = await res.json();
        setFileTypes(data);
      } catch (error) {
        console.error('Failed to fetch filetypes:', error);
      }
    })();
  }, []);

  // fetch extension data when ready
  useEffect(() => {
    if (!treeMapReady) return;

    (async () => {
      try {
        const rootRes = await fetch(`${import.meta.env.VITE_APP_API_URL}/files/${root}`, { method: 'GET' });
        const rootData = await rootRes.json();

        const dataRes = await fetch(`${import.meta.env.VITE_APP_API_URL}/files/extensions?root=${rootData.path}`, { method: 'GET' });
        let extensionsData = await dataRes.json();

        const colourRes = await fetch(`${import.meta.env.VITE_APP_API_URL}/treemap/colours/extension`, { method: 'GET' });
        const colourData = await colourRes.json();

        extensionsData.forEach((row) => {
          row.colour = colourData[row.extension];
          row.description = fileTypes?.['.' + row.extension]?.description;
        });

        setData(extensionsData);
      } catch (error) {
        console.error('Failed to fetch extension data:', error);
      }
    })();

  }, [treeMapReady]);

  // resizing column widths by dragging
  useEffect(() => {
    const handleMouseMove = (e) => {
      if (resizing.index === null) return;

      document.body.style.cursor = 'col-resize';
      document.body.style.userSelect = 'none';  // prevent highlighting text

      const deltaX = e.clientX - resizing.startX;
      setColumnWidths(prevWidths => {
        const newWidths = [...prevWidths];
        newWidths[resizing.index] = Math.max(80, resizing.startWidth + deltaX);
        return newWidths;
      })
    };

    const handleMouseUp = () => {
      if (resizing.index === null) return;

      // setTimeout because it causes rerenders and interferes with DirectoryButton
      setTimeout(() => {
        setResizing({index: null, startX: 0, startWidth: 0});
      }, 0);
      document.body.style.cursor = 'default';
      document.body.style.userSelect = 'text';
    };

    window.addEventListener('mousemove', handleMouseMove);
    window.addEventListener('mouseup', handleMouseUp);

    return () => {
      window.removeEventListener('mousemove', handleMouseMove);
      window.removeEventListener('mouseup', handleMouseUp);
      document.body.style.cursor = 'default';
      document.body.style.userSelect = 'auto';
    };
  }, [resizing]);

  // rerender grid when resizing
  useEffect(() => {
    gridRef.current?.resetAfterColumnIndex(0);
  }, [columnWidths]);

  const Cell = ({ rowIndex, columnIndex, style }) => {

    const extensionData = data[rowIndex];
    const column = columnMapping[columnIndex];

    let value = extensionData[column];

    const isColourCol = column === 'colour';
    const isSelected = extensionData.extension === selectedExtension;
    
    const backgroundColor = isColourCol && typeof value === 'number'
      ? `rgb(${(value >> 24) & 0xff}, ${(value >> 16) & 0xff}, ${(value >> 8) & 0xff})`
      : 'lightgray';

    if (isColourCol) value = '';

    const cellClass = `cell ${isSelected ? 'cell--selected' : ''}`

    return (
      <div
        className={cellClass}
        style={{...style}}
        onClick={() => {
          setSelectedExtension(extensionData.extension);
        }}
      >
        {isColourCol && (
          <div
            className='colour-cell'
            style={{
              background: isColourCol ? `radial-gradient(circle, white 0%, ${backgroundColor} 100%)` : ''
            }}>
          </div>
        )}
        {value}
      </div>
    );
  };

  return (
    <div className='extension-info-container'>
      <div ref={headingRef} className='column-heading-container'>
        {/* Column Headings */}
        {columnMapping.map((col, index) => (
          <div key={col} className='column-heading' style={{width: columnWidths[index]}}>
            <span>{col.charAt(0).toUpperCase() + col.slice(1)}</span>
            {(index != columnMapping.length) && (
              <div
                className='column-resize-box'
                onMouseDown={e => {
                  setResizing({
                    index,
                    startX: e.clientX,
                    startWidth: columnWidths[index],
                  })
                }}
              >
                &#8942;
              </div>
            )}
          </div>
        ))}
      </div>
      {treeMapReady ? (
        <AutoSizer>
          {({height, width}) => (
            <Grid                
              ref={gridRef}
              width={width - 5}
              height={height - 35}
              columnCount={columnMapping.length}
              columnWidth={index => columnWidths[index]}
              rowCount={data.length}
              rowHeight={() => 20}
              onScroll={({scrollLeft}) => {
                if (headingRef.current) {
                  headingRef.current.scrollLeft = scrollLeft;
                }
              }}
            >
              {Cell}
            </Grid>
          )}
        </AutoSizer>
      ) : (
        <LinearProgress sx={{height : 10}} />
      )}
    </div>
  );
}

export default ExtensionInfo;