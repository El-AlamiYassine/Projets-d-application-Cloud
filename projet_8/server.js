const http = require('http');
const fs = require('fs');
const path = require('path');

const hostname = '0.0.0.0';
const port = 3000;

const server = http.createServer((req, res) => {
  if (req.url === '/' || req.url === '/index.html') {
    // Serve the HTML page
    res.statusCode = 200;
    res.setHeader('Content-Type', 'text/html');
    
    const html = `
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hello World - New Project</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
        }
        .container {
            text-align: center;
            padding: 2rem;
            background-color: white;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
        }
        h1 {
            color: #2c3e50;
            font-size: 3rem;
            margin-bottom: 1rem;
        }
        p {
            color: #7f8c8d;
            font-size: 1.2rem;
        }
        .footer {
            margin-top: 2rem;
            color: #95a5a6;
            font-size: 0.9rem;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Hello World!</h1>
        <p>Welcome to my new Node.js application</p>
        <p>Server is running on port ${port}</p>
        <div class="footer">
            Powered by Node.js
        </div>
    </div>
</body>
</html>
    `;
    
    res.end(html);
  } else {
    // Handle 404 for other routes
    res.statusCode = 404;
    res.setHeader('Content-Type', 'text/html');
    res.end(`
      <!DOCTYPE html>
      <html>
      <head>
        <title>Page Not Found</title>
      </head>
      <body>
        <h1>404 - Page Not Found</h1>
        <p>Go <a href="/">home</a></p>
      </body>
      </html>
    `);
  }
});

server.listen(port, hostname, () => {
  console.log(`New Hello World Server running at http://${hostname}:${port}/`);
});

module.exports = server;