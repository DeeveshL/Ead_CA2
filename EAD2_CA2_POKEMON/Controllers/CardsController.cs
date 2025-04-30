using EAD2_CA2_POKEMON.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

[ApiController]
[Route("api/[controller]")]
public class CardsController : ControllerBase
{
    private readonly AppDbContext _context;

    public CardsController(AppDbContext context)
    {
        _context = context;
    }

    // POST: api/cards
    [HttpPost]
    public async Task<IActionResult> CreateCard([FromBody] Cards card)
    {
        bool exists = await _context.Cards.AnyAsync(c =>
            c.Name == card.Name &&
            c.Expansion == card.Expansion &&
            c.ExpansionId == card.ExpansionId);

        if (exists)
            return Conflict("Card already exists");

        card.Id = Guid.NewGuid();
        _context.Cards.Add(card);
        await _context.SaveChangesAsync();

        return CreatedAtAction(nameof(GetCard), new { id = card.Id }, card);
    }

    // GET: api/cards/search?name={name}&expansion={expansion}&expansionId={expansionId}
    [HttpGet("search")]
    public async Task<ActionResult<IEnumerable<Cards>>> SearchCards(
        [FromQuery] string? name,
        [FromQuery] string? expansion,
        [FromQuery] int? expansionId)
    {
        var query = _context.Cards.AsQueryable();

        if (!string.IsNullOrEmpty(name))
            query = query.Where(c => c.Name.ToLower().Contains(name.ToLower()));

        if (!string.IsNullOrEmpty(expansion))
            query = query.Where(c => c.Expansion.ToLower().Contains(expansion.ToLower()));

        if (expansionId.HasValue)
            query = query.Where(c => c.ExpansionId == expansionId);

        var results = await query.ToListAsync();

        if (results.Count == 0)
            return NotFound("No cards matched your search.");

        return Ok(results);
    }



    // GET: api/cards
    [HttpGet]
    public async Task<ActionResult<IEnumerable<Cards>>> GetAllCards()
    {
        return await _context.Cards.ToListAsync();
    }

    // GET: api/cards/{id}
    [HttpGet("{id}")]
    public async Task<ActionResult<Cards>> GetCard(Guid id)
    {
        var card = await _context.Cards.FindAsync(id);
        if (card == null) return NotFound();
        return card;
    }
}
