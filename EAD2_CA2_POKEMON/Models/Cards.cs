

namespace EAD2_CA2_POKEMON.Models;


public class Cards
{
    public Guid Id { get; set; } = Guid.NewGuid();
    public string Name { get; set; } = string.Empty;
    public string Expansion { get; set; } = string.Empty;
    public int ExpansionId { get; set; }

    public List<CollectionCard> CollectionCards { get; set; } = new();
}
