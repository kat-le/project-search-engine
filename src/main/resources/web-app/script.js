document.getElementById("searchBtn").addEventListener("click", () => {
    const query = document.getElementById("query").value.trim();
    if (!query) return;

    fetch(`/search?q=${encodeURIComponent(query)}`)
        .then(resp => resp.json())
        .then(data => {
            const resultsList = document.getElementById("results");
            resultsList.innerHTML = ""; // clear previous results

            if (data.length === 0) {
                const li = document.createElement("li");
                li.textContent = "No results found.";
                li.className = "list-group-item";
                resultsList.appendChild(li);
                return;
            }

            data.forEach(item => {
                const li = document.createElement("li");
                li.className = "list-group-item";
                li.innerHTML = `
                    <strong><a href="${item.location}" target="_blank">${item.location}</a></strong><br>
                    Score: ${item.score} | Matches: ${item.matches}
                `;
                resultsList.appendChild(li);
            });
        })
        .catch(err => console.error("Error fetching search results:", err));
});