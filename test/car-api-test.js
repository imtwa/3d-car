fetch("http://localhost:8080/car/brand/page", {
  headers: {
    "content-type": "application/json;charset=UTF-8",
  },
  body: '{"pageNum":1,"pageSize":10}',
  method: "POST",
})
  .then((res) => res.json())
  .then((res) => {
    console.log(res);
  });
